package kr.bb.store.domain.coupon.controller;

import bloomingblooms.response.CommonResponse;
import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.controller.request.TotalAmountRequest;
import kr.bb.store.domain.coupon.controller.request.UserInfoRequest;
import kr.bb.store.domain.coupon.controller.response.CouponIssuerResponse;
import kr.bb.store.domain.coupon.controller.response.CouponsForOwnerResponse;
import kr.bb.store.domain.coupon.controller.response.CouponsForUserResponse;
import kr.bb.store.domain.coupon.facade.CouponFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class CouponController {
    private final CouponFacade couponFacade;

    @PostMapping("/{storeId}/coupons")
    public void createCoupon(@PathVariable Long storeId,
                             @RequestBody CouponCreateRequest couponCreateRequest) {
        couponFacade.createCoupon(storeId, couponCreateRequest);
    }

    @PutMapping("/{storeId}/coupons/{couponId}")
    public void editCoupon(@PathVariable Long storeId, @PathVariable Long couponId,
                           @RequestBody CouponEditRequest couponEditRequest) {
        couponFacade.editCoupon(storeId, couponId, couponEditRequest);
    }

    @DeleteMapping("/{storeId}/coupons/{couponId}")
    public void deleteCoupon(@PathVariable Long storeId, @PathVariable Long couponId) {
        couponFacade.softDeleteCoupon(storeId, couponId);
    }

    @GetMapping("/{storeId}/coupons")
    public CommonResponse<CouponsForOwnerResponse> coupons(@PathVariable Long storeId) {
        return CommonResponse.success(couponFacade.getAllStoreCoupons(storeId));
    }

    @PostMapping("/coupons/{couponId}")
    public void downloadCoupon(@PathVariable Long couponId,
                               @RequestHeader(value = "userId") Long userId,
                               @RequestBody UserInfoRequest userInfoRequest) {
        couponFacade.downloadCoupon(userId, couponId, userInfoRequest.getNickname(), userInfoRequest.getPhoneNumber(), LocalDate.now());
    }

    @PostMapping("/{storeId}/coupons/all")
    public void downloadAllCoupons(@PathVariable Long storeId,
                                   @RequestHeader(value = "userId") Long userId,
                                   @RequestBody UserInfoRequest userInfoRequest) {
        couponFacade.downloadAllCoupons(userId, storeId, userInfoRequest.getNickname(), userInfoRequest.getPhoneNumber(), LocalDate.now());
    }

    @GetMapping("/{storeId}/coupons/product")
    public CommonResponse<CouponsForUserResponse> storeCouponsForUser(@PathVariable Long storeId,
            @RequestHeader(value = "userId", required = false) Long userId) {
        return CommonResponse.success(couponFacade.getAllStoreCouponsForUser(userId, storeId));
    }

    @PostMapping("/{storeId}/coupons/payment")
    public CommonResponse<CouponsForUserResponse> couponsInPaymentStep(@PathVariable Long storeId,
            @RequestHeader(value = "userId") Long userId,
            @RequestBody TotalAmountRequest totalAmountRequest) {
        return CommonResponse.success(couponFacade.getAvailableCouponsInPayment(totalAmountRequest, userId, storeId));
    }

    @GetMapping("/coupons/my")
    public CommonResponse<CouponsForUserResponse> myCoupons(@RequestHeader(value = "userId") Long userId) {
        return CommonResponse.success(couponFacade.getMyValidCoupons(userId));
    }

    @GetMapping("/coupons/{couponId}/members")
    public CommonResponse<CouponIssuerResponse> couponMembers(@RequestHeader(value = "userId") Long userId,
            @PathVariable Long couponId, Pageable pageable) {
        return CommonResponse.success(couponFacade.getCouponMembers(userId, couponId, pageable));
    }
}
