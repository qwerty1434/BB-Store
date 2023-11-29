package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.dto.CouponDto;
import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.dto.CouponWithIssueStatusDto;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.exception.CouponNotFoundException;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponReader {
    private final CouponRepository couponRepository;

    public Coupon read(Long couponId) {
        return couponRepository.findById(couponId).orElseThrow(CouponNotFoundException::new);
    }

    public List<CouponForOwnerDto> readCouponsForOwner(Long storeId) {
        return couponRepository.findAllDtoByStoreId(storeId);
    }

    public List<Coupon> readStoresAllValidateCoupon(Long storeId, LocalDate now) {
        return couponRepository.findAllDownloadableCouponsByStoreId(storeId, now);
    }

    public List<CouponWithIssueStatusDto> readStoreCouponsForUser(Long userId, Long storeId, LocalDate now) {
        return couponRepository.findStoreCouponsForUser(userId, storeId, now);
    }

    public List<CouponDto> readAvailableCouponsInStore(Long userId, Long storeId, LocalDate readDate) {
        return couponRepository.findAvailableCoupons(userId, storeId, readDate);
    }

    public List<CouponDto> readMyValidCoupons(Long userId, LocalDate readDate) {
        return couponRepository.findMyValidCoupons(userId, readDate);
    }
}
