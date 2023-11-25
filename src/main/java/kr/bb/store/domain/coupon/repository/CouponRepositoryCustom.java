package kr.bb.store.domain.coupon.repository;

import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;

import java.util.List;

public interface CouponRepositoryCustom {
    List<CouponForOwnerDto> findAllDtoByStoreId(Long storeId);
}
