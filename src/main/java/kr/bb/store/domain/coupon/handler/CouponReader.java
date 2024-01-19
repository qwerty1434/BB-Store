package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.dto.CouponDto;
import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.dto.CouponWithAvailabilityDto;
import kr.bb.store.domain.coupon.dto.CouponWithIssueStatusDto;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.exception.CouponNotFoundException;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import kr.bb.store.util.RedisOperation;
import kr.bb.store.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CouponReader {
    private final CouponRepository couponRepository;
    private final RedisOperation redisOperation;

    public Coupon read(Long couponId) {
        return couponRepository.findById(couponId).orElseThrow(CouponNotFoundException::new);
    }

    public List<CouponForOwnerDto> readCouponsForOwner(Long storeId, LocalDate now) {
        return couponRepository.findAllDtoByStoreId(storeId, now);
    }

    public List<Coupon> readStoresAllValidateCoupon(Long storeId, LocalDate now) {
        return couponRepository.findAllValidateCouponsByStoreId(storeId, now);
    }

    public List<CouponWithIssueStatusDto> readStoreCouponsForUser(Long userId, Long storeId, LocalDate now) {
        return couponRepository.findStoreCouponsForUser(userId, storeId, now).stream()
                .filter(dto -> {
                    String redisKey = RedisUtils.makeRedisKey(dto.getCouponCode(), dto.getCouponId().toString());
                    Long issueCount = redisOperation.count(redisKey);
                    return issueCount <= dto.getLimitCount();
                })
                .collect(Collectors.toList());
    }

    public List<CouponWithAvailabilityDto> readAvailableCouponsInStore(Long totalAmount, Long userId, Long storeId,
                                                                       LocalDate readDate) {
        return couponRepository.findAvailableCoupons(totalAmount, userId, storeId, readDate);
    }

    public List<CouponDto> readMyValidCoupons(Long userId, LocalDate readDate) {
        return couponRepository.findMyValidCoupons(userId, readDate);
    }

    public Integer readMyValidCouponCount(Long userId, LocalDate readDate) {
        return couponRepository.findMyValidCouponCount(userId, readDate);
    }
}
