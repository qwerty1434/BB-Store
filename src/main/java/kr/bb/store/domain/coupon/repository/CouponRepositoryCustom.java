package kr.bb.store.domain.coupon.repository;

import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.entity.Coupon;

import java.util.List;

public interface CouponRepositoryCustom {
    List<CouponForOwnerDto> findAllDtoByStoreId(Long storeId);
    List<Coupon> findAllValidateCouponsByStoreId(Long storeId);
}
