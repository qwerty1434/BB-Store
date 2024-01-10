package kr.bb.store.domain.coupon.service;

import bloomingblooms.domain.order.ValidatePriceDto;
import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.controller.request.TotalAmountRequest;
import kr.bb.store.domain.coupon.controller.response.CouponIssuerResponse;
import kr.bb.store.domain.coupon.dto.*;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.exception.CouponInconsistencyException;
import kr.bb.store.domain.coupon.exception.UnAuthorizedCouponException;
import kr.bb.store.domain.coupon.handler.*;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.StoreReader;
import kr.bb.store.util.RedisOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static kr.bb.store.util.RedisUtils.makeRedisKey;

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

        String redisKey = makeRedisKey(coupon);
        redisOperation.addAndSetExpr(redisKey, coupon.getEndDate().plusDays(1));
    }

    @Transactional
    public void editCoupon(Long storeId, Long couponId, CouponEditRequest couponEditRequest) {
        Coupon coupon = couponReader.read(couponId);
        validateCouponAuthorization(coupon,storeId);
        couponManager.edit(coupon, couponEditRequest.toDto());

        String redisKey = makeRedisKey(coupon);
        redisOperation.setExpr(redisKey, couponEditRequest.getEndDate());
    }

    @Transactional
    public void softDeleteCoupon(Long storeId, Long couponId) {
        Coupon coupon = couponReader.read(couponId);
        validateCouponAuthorization(coupon,storeId);
        couponManager.softDelete(coupon);
    }

    @Transactional
    public void downloadCoupon(Long userId, Long couponId, String nickname, String phoneNumber, LocalDate now) {
        Coupon coupon = couponReader.read(couponId);
        couponIssuer.issueCoupon(coupon, userId, nickname, phoneNumber, now);
    }

    @Transactional
    public void downloadAllCoupons(Long userId, Long storeId, String nickname, String phoneNumber, LocalDate now) {
        List<Coupon> coupons = couponReader.readStoresAllValidateCoupon(storeId, now);
        couponIssuer.issuePossibleCoupons(coupons, userId, nickname, phoneNumber, now);
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

    @Transactional
    public void unUseAllCoupons(List<Long> couponIds, Long userId) {
        couponIds.forEach(couponId -> {
            IssuedCoupon issuedCoupon = issuedCouponReader.read(couponId,userId);
            couponManager.unUse(issuedCoupon);
        });
    }

    public List<CouponForOwnerDto> getAllStoreCoupons(Long storeId) {
        return couponReader.readCouponsForOwner(storeId);
    }

    public List<CouponWithIssueStatusDto> getAllStoreCouponsForUser(Long userId, Long storeId, LocalDate now) {
        return couponReader.readStoreCouponsForUser(userId, storeId, now);
    }

    public List<CouponWithAvailabilityDto> getAvailableCouponsInPayment(TotalAmountRequest totalAmountRequest,
                                                               Long userId, Long storeId, LocalDate now) {
        return couponReader.readAvailableCouponsInStore(totalAmountRequest.getTotalAmount(), userId, storeId, now);
    }

    public List<CouponDto> getMyValidCoupons(Long userId, LocalDate now) {
        return couponReader.readMyValidCoupons(userId, now);
    }

    public CouponIssuerResponse getCouponMembers(Long userId, Long couponId, Pageable pageable) {
        Coupon coupon = couponReader.read(couponId);
        if(!coupon.getStore().getStoreManagerId().equals(userId)) {
            throw new UnAuthorizedCouponException();
        }

        List<IssuedCouponDto> issuedCoupons = issuedCouponReader.readByCouponId(couponId, pageable)
                .stream().map(IssuedCouponDto::fromEntity).collect(Collectors.toList());
        long count = issuedCouponReader.countByCouponId(couponId);

        return CouponIssuerResponse.of(issuedCoupons, count);
    }

    public Integer getMyAvailableCouponCount(Long userId, LocalDate now) {
        return couponReader.readMyValidCouponCount(userId, now);
    }

    public void validateCouponPrice(List<ValidatePriceDto> validatePriceDtos) {
        validatePriceDtos.stream()
                .filter(dto -> dto.getCouponId() != null)
                .forEach(dto -> {
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
