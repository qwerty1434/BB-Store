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
import kr.bb.store.util.luascript.CouponLockExecutor;
import kr.bb.store.util.luascript.RedisLuaScriptExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static kr.bb.store.util.RedisUtils.makeRedisKey;
import static kr.bb.store.util.luascript.LockScript.script;

@Component
public class CouponIssuer {
    private final IssuedCouponRepository issuedCouponRepository;
    private final RedisLuaScriptExecutor redisLuaScriptExecutor;
    private final RedisOperation redisOperation;

    public CouponIssuer(IssuedCouponRepository issuedCouponRepository, CouponLockExecutor couponLockExecutor, RedisOperation redisOperation) {
        this.issuedCouponRepository = issuedCouponRepository;
        this.redisLuaScriptExecutor = couponLockExecutor;
        this.redisOperation = redisOperation;
    }

    public IssuedCoupon issueCoupon(Coupon coupon, Long userId, String nickname, String phoneNumber, LocalDate issueDate) {
        if(coupon.getIsDeleted()) throw new DeletedCouponException();
        if(coupon.isExpired(issueDate)) throw new ExpiredCouponException();

        String redisKey = makeRedisKey(coupon.getCouponCode(), coupon.getId().toString());
        String redisValue = userId.toString();
        Integer limitCnt = coupon.getLimitCount();
        if(isDuplicated(redisKey, redisValue)) throw new AlreadyIssuedCouponException();

        boolean issuable = (Boolean)redisLuaScriptExecutor.execute(script, redisKey, redisValue, limitCnt);
        if(issuable) {
            return issuedCouponRepository.save(makeIssuedCoupon(coupon,userId,nickname,phoneNumber));
        }
        throw new CouponOutOfStockException();
    }

    public void issuePossibleCoupons(List<Coupon> coupons, Long userId, String nickname, String phoneNumber, LocalDate issueDate) {
        final String redisValue = userId.toString();

        coupons.stream()
                .filter(Predicate.not(Coupon::getIsDeleted))
                .filter(Predicate.not(coupon -> coupon.isExpired(issueDate)))
                .filter(Predicate.not(coupon -> {
                    String redisKey = makeRedisKey(coupon.getCouponCode(), coupon.getId().toString());
                    return isDuplicated(redisKey,redisValue);
                }))
                .filter(coupon -> {
                    String redisKey = makeRedisKey(coupon.getCouponCode(), coupon.getId().toString());
                    Integer limitCnt = coupon.getLimitCount();
                    return (Boolean)redisLuaScriptExecutor.execute(script, redisKey, redisValue, limitCnt);
                })
                .forEach(coupon -> issuedCouponRepository.save(makeIssuedCoupon(coupon,userId,nickname,phoneNumber)));
    }

    private IssuedCoupon makeIssuedCoupon(Coupon coupon, Long userId, String nickname, String phoneNumber) {
        return IssuedCoupon.builder()
                .id(makeIssuedCouponId(coupon.getId(), userId))
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .coupon(coupon)
                .build();
    }

    private IssuedCouponId makeIssuedCouponId(Long couponId, Long userId) {
        return IssuedCouponId.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
    }

    private boolean isDuplicated(String redisKey, String value) {
        return redisOperation.contains(redisKey, value);
    }

}
