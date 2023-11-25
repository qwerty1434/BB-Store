package kr.bb.store.domain.coupon.controller;

import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/{storeId}/coupons")
    public ResponseEntity createCoupon(@PathVariable Long storeId,
                                       @RequestBody CouponCreateRequest couponCreateRequest) {
        couponService.createCoupon(storeId, couponCreateRequest);

        return ResponseEntity.ok().build();
    }
}
