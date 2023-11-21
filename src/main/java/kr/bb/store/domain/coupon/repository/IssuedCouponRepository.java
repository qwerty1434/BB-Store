package kr.bb.store.domain.coupon.repository;

import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, IssuedCouponId>,IssuedCouponRepositoryCustom {
}
