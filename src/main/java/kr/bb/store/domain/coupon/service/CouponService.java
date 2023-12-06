package kr.bb.store.domain.coupon.service;

import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.controller.response.CouponsForOwnerResponse;
import kr.bb.store.domain.coupon.controller.response.CouponsForUserResponse;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.exception.UnAuthorizedCouponException;
import kr.bb.store.domain.coupon.handler.*;
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
    private final IssuedCouponReader issuedCouponReader;
    private final CouponIssuer couponIssuer;
    private final StoreReader storeReader;

    @Transactional
    public void createCoupon(Long storeId, CouponCreateRequest couponCreateRequest) {
        Store store = storeReader.findStoreById(storeId);
        couponCreator.create(store, couponCreateRequest.toDto());
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

    @Transactional
    public void downloadCoupon(Long userId, Long couponId, LocalDate now) {
        Coupon coupon = couponReader.read(couponId);
        couponIssuer.issueCoupon(coupon, userId, now);
    }

    @Transactional
    public void downloadAllCoupons(Long userId, Long storeId, LocalDate now) {
        List<Coupon> coupons = couponReader.readStoresAllValidateCoupon(storeId, now);
        couponIssuer.issuePossibleCoupons(coupons, userId, now);
    }

    public CouponsForOwnerResponse getAllStoreCoupons(Long storeId) {
        return CouponsForOwnerResponse.builder()
                .data(couponReader.readCouponsForOwner(storeId))
                .build();
    }

    public CouponsForUserResponse getAllStoreCouponsForUser(Long userId, Long storeId, LocalDate now) {
        return CouponsForUserResponse.builder()
                .data(couponReader.readStoreCouponsForUser(userId, storeId, now))
                .build();
    }

    public CouponsForUserResponse getAvailableCouponsInPayment(Long userId, Long storeId) {
        LocalDate now = LocalDate.now();
        return CouponsForUserResponse.builder()
                .data(couponReader.readAvailableCouponsInStore(userId, storeId, now))
                .build();
    }

    public CouponsForUserResponse getMyValidCoupons(Long userId) {
        LocalDate now = LocalDate.now();
        return CouponsForUserResponse.builder()
                .data(couponReader.readMyValidCoupons(userId, now))
                .build();
    }

    public void useCoupon(Long couponId, Long userId, LocalDate useDate) {
        IssuedCoupon issuedCoupon = issuedCouponReader.read(couponId,userId);
        couponManager.use(issuedCoupon, useDate);
    }

    private void validateCouponAuthorization(Coupon coupon, Long storeId) {
        if(!coupon.getStore().getId().equals(storeId)) throw new UnAuthorizedCouponException();
    }

}
