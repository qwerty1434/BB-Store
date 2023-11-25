package kr.bb.store.domain.coupon.service;

import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.handler.CouponCreator;
import kr.bb.store.domain.coupon.handler.CouponManager;
import kr.bb.store.domain.coupon.handler.CouponReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {
    private final CouponCreator couponCreator;
    private final CouponManager couponManager;
    private final CouponReader couponReader;
    public Coupon createCoupon(Long storeId, CouponCreateRequest couponCreateRequest) {
        return couponCreator.create(storeId, couponCreateRequest.toDto());
    }

    public void editCoupon(Long storeId, Long couponId, CouponEditRequest couponEditRequest) {
        Coupon coupon = couponReader.read(couponId);
        couponManager.edit(coupon,couponEditRequest.toDto());
    }
}
