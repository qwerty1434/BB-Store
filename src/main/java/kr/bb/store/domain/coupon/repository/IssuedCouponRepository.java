package kr.bb.store.domain.coupon.repository;

import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, IssuedCouponId> {
    @Query(value = "select * from issued_coupon where user_id = :userId and is_used = false",nativeQuery = true)
    List<IssuedCoupon> findUsableCouponsByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM issued_coupon WHERE coupon_id = :couponId ORDER BY created_at LIMIT :pageSize OFFSET :offset", nativeQuery = true)
    List<IssuedCoupon> findByCouponId(@Param("couponId") Long couponId, @Param("offset") long offset, @Param("pageSize") int pageSize);

    long countByCouponId(Long couponId);
}
