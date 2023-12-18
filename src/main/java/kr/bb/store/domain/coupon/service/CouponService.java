package kr.bb.store.domain.coupon.service;

import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.controller.request.TotalAmountRequest;
import kr.bb.store.domain.coupon.controller.response.CouponsForOwnerResponse;
import kr.bb.store.domain.coupon.controller.response.CouponsForUserResponse;
import kr.bb.store.domain.coupon.dto.CouponDto;
import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.dto.CouponWithAvailabilityDto;
import kr.bb.store.domain.coupon.dto.CouponWithIssueStatusDto;
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
        List<CouponForOwnerDto> couponForOwnerDtos = couponReader.readCouponsForOwner(storeId);
        return CouponsForOwnerResponse.from(couponForOwnerDtos);
    }

    public CouponsForUserResponse getAllStoreCouponsForUser(Long userId, Long storeId, LocalDate now) {
        List<CouponWithIssueStatusDto> couponWithIssueStatusDtos =
                couponReader.readStoreCouponsForUser(userId, storeId, now);
        return CouponsForUserResponse.from(couponWithIssueStatusDtos);
    }

    public CouponsForUserResponse getAvailableCouponsInPayment(TotalAmountRequest totalAmountRequest,
                                                               Long userId, Long storeId, LocalDate now) {
        List<CouponWithAvailabilityDto> couponWithAvailabilityDtos =
                couponReader.readAvailableCouponsInStore(totalAmountRequest.getTotalAmount(), userId, storeId, now);
        return CouponsForUserResponse.from(couponWithAvailabilityDtos);
    }

    public CouponsForUserResponse getMyValidCoupons(Long userId, LocalDate now) {
        List<CouponDto> couponDtos = couponReader.readMyValidCoupons(userId, now);
        return CouponsForUserResponse.from(couponDtos);
    }

    public void useCoupon(Long couponId, Long userId, LocalDate useDate) {
        IssuedCoupon issuedCoupon = issuedCouponReader.read(couponId,userId);
        couponManager.use(issuedCoupon, useDate);
    }

    public Integer getAvailableCouponCount(Long userId, LocalDate now) {
        return couponReader.readMyValidCouponCount(userId, now);
    }

    private void validateCouponAuthorization(Coupon coupon, Long storeId) {
        if(!coupon.getStore().getId().equals(storeId)) throw new UnAuthorizedCouponException();
    }

}
