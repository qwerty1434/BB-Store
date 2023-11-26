package kr.bb.store.domain.coupon.service;

import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.controller.response.CouponsForOwnerResponse;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.exception.UnAuthorizedCouponException;
import kr.bb.store.domain.coupon.handler.CouponCreator;
import kr.bb.store.domain.coupon.handler.CouponIssuer;
import kr.bb.store.domain.coupon.handler.CouponManager;
import kr.bb.store.domain.coupon.handler.CouponReader;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.StoreReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {
    private final CouponCreator couponCreator;
    private final CouponManager couponManager;
    private final CouponReader couponReader;
    private final CouponIssuer couponIssuer;
    private final StoreReader storeReader;

    @Transactional
    public Coupon createCoupon(Long storeId, CouponCreateRequest couponCreateRequest) {
        Store store = storeReader.findStoreById(storeId);
        return couponCreator.create(store, couponCreateRequest.toDto());
    }

    @Transactional
    public void editCoupon(Long storeId, Long couponId, CouponEditRequest couponEditRequest) {
        Coupon coupon = couponReader.read(couponId);
        validateCouponAuthorization(coupon,storeId);
        couponManager.edit(coupon,couponEditRequest.toDto());
    }

    @Transactional
    public void softDeleteCoupon(Long storeId, Long couponId) {
        Coupon coupon = couponReader.read(couponId);
        validateCouponAuthorization(coupon,storeId);
        couponManager.softDelete(coupon);
    }

    public CouponsForOwnerResponse getAllStoreCoupons(Long storeId) {
        return CouponsForOwnerResponse.builder()
                .data(couponReader.readCouponsForOwner(storeId))
                .build();
    }

    @Transactional
    public void downloadCoupon(Long userId, Long couponId, LocalDate issueDate) {
        Coupon coupon = couponReader.read(couponId);
        couponIssuer.issueCoupon(coupon, userId, issueDate);
    }

    @Transactional
    public void downloadAllCoupons(Long userId, Long storeId, LocalDate issueDate) {
        List<Coupon> coupons = couponReader.readStoresAllValidateCoupon(storeId);
        couponIssuer.issuePossibleCoupons(coupons, userId, issueDate);
    }



    private void validateCouponAuthorization(Coupon coupon, Long storeId) {
        if(!coupon.getStore().getId().equals(storeId)) throw new UnAuthorizedCouponException();
    }

}
