package kr.bb.store.domain.coupon.repository;

import kr.bb.store.domain.coupon.dto.CouponDto;
import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.dto.CouponWithAvailabilityDto;
import kr.bb.store.domain.coupon.dto.CouponWithIssueStatusDto;
import kr.bb.store.domain.coupon.entity.Coupon;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepositoryCustom {
    List<CouponForOwnerDto> findAllDtoByStoreId(Long storeId, LocalDate now);
    List<Coupon> findAllDownloadableCouponsByStoreId(Long storeId, LocalDate now);
    List<CouponWithIssueStatusDto> findStoreCouponsForUser(Long userId, Long storeId, LocalDate now);
    List<CouponWithAvailabilityDto> findAvailableCoupons(Long totalAmount, Long userId, Long storeId, LocalDate now);
    List<CouponDto> findMyValidCoupons(Long userId, LocalDate now);

    Integer findMyValidCouponCount(Long userId, LocalDate now);
}
