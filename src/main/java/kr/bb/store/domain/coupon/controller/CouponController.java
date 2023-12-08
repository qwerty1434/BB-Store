package kr.bb.store.domain.coupon.controller;

import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.controller.request.TotalAmountRequest;
import kr.bb.store.domain.coupon.controller.response.CouponsForOwnerResponse;
import kr.bb.store.domain.coupon.controller.response.CouponsForUserResponse;
import kr.bb.store.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/{storeId}/coupons")
    public ResponseEntity<Void> createCoupon(@PathVariable Long storeId,
                                       @RequestBody CouponCreateRequest couponCreateRequest) {
        couponService.createCoupon(storeId, couponCreateRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{storeId}/coupons/{couponId}")
    public ResponseEntity<Void> editCoupon(@PathVariable Long storeId, @PathVariable Long couponId,
                                     @RequestBody CouponEditRequest couponEditRequest) {
        couponService.editCoupon(storeId, couponId, couponEditRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{storeId}/coupons/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long storeId, @PathVariable Long couponId) {
        couponService.softDeleteCoupon(storeId, couponId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{storeId}/coupons")
    public ResponseEntity<CouponsForOwnerResponse> coupons(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(couponService.getAllStoreCoupons(storeId));
    }

    @PostMapping("/coupons/{couponId}")
    public ResponseEntity<Void> downloadCoupon(@PathVariable Long couponId,
                                         @RequestHeader(value = "userId") Long userId) {
        couponService.downloadCoupon(userId, couponId, LocalDate.now());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{storeId}/coupons/all")
    public ResponseEntity<Void> downloadAllCoupons(@PathVariable Long storeId,
                                             @RequestHeader(value = "userId") Long userId) {
        couponService.downloadAllCoupons(userId, storeId, LocalDate.now());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{storeId}/coupons/product")
    public ResponseEntity<CouponsForUserResponse> storeCouponsForUser(@PathVariable Long storeId,
                                                                      @RequestHeader(value = "userId") Long userId) {

        LocalDate now = LocalDate.now();
        return ResponseEntity.ok().body(couponService.getAllStoreCouponsForUser(userId, storeId, now));
    }

    @PostMapping("/{storeId}/coupons/payment")
    public ResponseEntity<CouponsForUserResponse> couponsInPaymentStep(@PathVariable Long storeId,
                                                                       @RequestHeader(value = "userId") Long userId,
                                                                       @RequestBody TotalAmountRequest totalAmountRequest) {

        LocalDate now = LocalDate.now();
        return ResponseEntity.ok().body(couponService.getAvailableCouponsInPayment(totalAmountRequest, userId, storeId, now));
    }

    @GetMapping("/coupons/my")
    public ResponseEntity<CouponsForUserResponse> myCoupons(@RequestHeader(value = "userId") Long userId) {

        LocalDate now = LocalDate.now();
        return ResponseEntity.ok().body(couponService.getMyValidCoupons(userId, now));
    }
}
