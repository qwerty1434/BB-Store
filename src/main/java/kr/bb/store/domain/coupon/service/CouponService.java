package kr.bb.store.domain.coupon.service;

import bloomingblooms.domain.order.ValidatePriceDto;
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
import kr.bb.store.domain.coupon.exception.CouponInconsistencyException;
import kr.bb.store.domain.coupon.exception.UnAuthorizedCouponException;
import kr.bb.store.domain.coupon.handler.*;
import kr.bb.store.domain.coupon.util.RedisUtils;
import kr.bb.store.domain.coupon.util.RedisOperation;
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
    private final RedisOperation redisOperation;

    @Transactional
    public void createCoupon(Long storeId, CouponCreateRequest couponCreateRequest) {
        Store store = storeReader.findStoreById(storeId);
        Coupon coupon = couponCreator.create(store, couponCreateRequest.toDto());

        String redisKey = RedisUtils.makeRedisKey(coupon);
        redisOperation.addAndSetExpr(redisKey, coupon.getEndDate().plusDays(1));
    }

    @Transactional
    public void editCoupon(Long storeId, Long couponId, CouponEditRequest couponEditRequest) {
        Coupon coupon = couponReader.read(couponId);
        validateCouponAuthorization(coupon,storeId);
        couponManager.edit(coupon, couponEditRequest.toDto());

        String redisKey = RedisUtils.makeRedisKey(coupon);
        redisOperation.setExpr(redisKey, couponEditRequest.getEndDate());
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

    @Transactional
    public void useCoupon(Long couponId, Long userId, LocalDate useDate) {
        IssuedCoupon issuedCoupon = issuedCouponReader.read(couponId,userId);
        couponManager.use(issuedCoupon, useDate);
    }

    @Transactional
    public void useAllCoupons(List<Long> couponIds, Long userId, LocalDate useDate) {
        couponIds.forEach(couponId -> {
            IssuedCoupon issuedCoupon = issuedCouponReader.read(couponId,userId);
            couponManager.use(issuedCoupon, useDate);
        });
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

    public Integer getMyAvailableCouponCount(Long userId, LocalDate now) {
        return couponReader.readMyValidCouponCount(userId, now);
    }

    public void validateCouponPrice(List<ValidatePriceDto> validatePriceDtos) {
        validatePriceDtos.forEach(dto -> {
            Coupon coupon = couponReader.read(dto.getCouponId());
            Long receivedPaymentPrice = dto.getActualAmount();
            Long receivedDiscountPrice = dto.getCouponAmount();
            if(!coupon.isRightPrice(receivedPaymentPrice, receivedDiscountPrice)) {
                throw new CouponInconsistencyException();
            }
        });
    }

    private void validateCouponAuthorization(Coupon coupon, Long storeId) {
        if(!coupon.getStore().getId().equals(storeId)) throw new UnAuthorizedCouponException();
    }

}
