package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import kr.bb.store.domain.coupon.exception.AlreadyIssuedCouponException;
import kr.bb.store.domain.coupon.exception.CouponOutOfStockException;
import kr.bb.store.domain.coupon.exception.DeletedCouponException;
import kr.bb.store.domain.coupon.exception.ExpiredCouponException;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
import kr.bb.store.util.RedisOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static kr.bb.store.util.RedisUtils.makeRedisKey;

@RequiredArgsConstructor
@Component
public class CouponIssuer {
    private final IssuedCouponRepository issuedCouponRepository;
    private final RedisOperation redisOperation;

    public IssuedCoupon issueCoupon(Coupon coupon, Long userId, LocalDate issueDate) {
        if(coupon.getIsDeleted()) throw new DeletedCouponException();
        if(coupon.isExpired(issueDate)) throw new ExpiredCouponException();

        String redisKey = makeRedisKey(coupon);
        String redisValue = userId.toString();
        if(isDuplicated(redisKey, redisValue)) throw new AlreadyIssuedCouponException();

        List<Long> result = (List) redisOperation.countAndSet(redisKey, redisValue);

        Integer limitCnt = coupon.getLimitCount();
        Long issueCount = result.get(0);
        if(isExhausted(limitCnt,issueCount)) {
            redisOperation.remove(redisKey, redisValue);
            throw new CouponOutOfStockException();
        }

        return issuedCouponRepository.save(makeIssuedCoupon(coupon,userId));
    }

    public void issuePossibleCoupons(List<Coupon> coupons, Long userId, LocalDate issueDate) {
        final String redisValue = userId.toString();

        coupons.stream()
                .filter(Predicate.not(Coupon::getIsDeleted))
                .filter(Predicate.not(coupon -> coupon.isExpired(issueDate)))
                .filter(Predicate.not(coupon -> {
                    String redisKey = makeRedisKey(coupon);
                    return isDuplicated(redisKey,redisValue);
                }))
                .filter(coupon -> {
                    String redisKey = makeRedisKey(coupon);

                    List<Long> result = (List) redisOperation.countAndSet(redisKey, redisValue);

                    Integer limitCnt = coupon.getLimitCount();
                    Long issueCount = result.get(0);
                    if(isExhausted(limitCnt,issueCount)) {
                        redisOperation.remove(redisKey, redisValue);
                        return false;
                    }
                    return true;
                })
                .forEach(coupon -> issuedCouponRepository.save(makeIssuedCoupon(coupon,userId)));
    }

    private IssuedCoupon makeIssuedCoupon(Coupon coupon, Long userId) {
        return IssuedCoupon.builder()
                .id(makeIssuedCouponId(coupon.getId(), userId))
                .coupon(coupon)
                .build();
    }

    private IssuedCouponId makeIssuedCouponId(Long couponId, Long userId) {
        return IssuedCouponId.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
    }

    /*
     * 모든 쿠폰은 생성시 expirationDate 설정을 위해 DUMMY_DATA를 넣어 redis에 등록됩니다.
     * 하나의 데이터가 더 들어있기 때문에 이를 고려해 '<=' 가 아닌 '<'로 개수를 비교해야
     * 쿠폰에 등록한 limitCount만큼 발급이 가능합니다.
     */
    private boolean isExhausted(Integer limitCount, Long issueCount) {
        return limitCount < issueCount;
    }

    private boolean isDuplicated(String redisKey, String value) {
        return redisOperation.contains(redisKey, value);
    }

}
