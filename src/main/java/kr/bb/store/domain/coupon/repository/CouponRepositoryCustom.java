package kr.bb.store.domain.coupon.repository;

import kr.bb.store.domain.coupon.dto.CouponDto;
import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.dto.CouponWithIssueStatusDto;
import kr.bb.store.domain.coupon.entity.Coupon;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepositoryCustom {
    List<CouponForOwnerDto> findAllDtoByStoreId(Long storeId);
    List<Coupon> findAllValidateCouponsByStoreId(Long storeId);
    List<CouponWithIssueStatusDto> findStoreCouponsForUser(Long userId, Long storeId);
    List<CouponDto> findAvailableCoupons(Long userId, Long storeId, LocalDate now);
    List<CouponDto> findMyValidCoupons(Long userId, LocalDate now);
}
