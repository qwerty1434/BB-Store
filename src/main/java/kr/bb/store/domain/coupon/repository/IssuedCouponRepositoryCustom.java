package kr.bb.store.domain.coupon.repository;

public interface IssuedCouponRepositoryCustom {
    Long findIssuedCountByCouponId(Long couponId);
}
