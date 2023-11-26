package kr.bb.store.domain.coupon.repository;

import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, IssuedCouponId>,IssuedCouponRepositoryCustom {
    @Query(value = "select * from issued_coupon where user_id = :userId",nativeQuery = true)
    List<IssuedCoupon> findAllByUserId(@Param("userId") Long userId);

}
