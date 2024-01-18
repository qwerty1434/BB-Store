package kr.bb.store.domain.coupon.repository;

import kr.bb.store.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon,Long>,CouponRepositoryCustom {
}
