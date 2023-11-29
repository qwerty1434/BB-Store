package kr.bb.store.domain.coupon.controller;

import kr.bb.store.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping(("/coupons"))
public class CouponFeignController {
    private final CouponService couponService;

    @PostMapping("/{couponId}/users/{userId}")
    public ResponseEntity userCoupon(@PathVariable Long couponId, @PathVariable Long userId) {
        LocalDate useDate = LocalDate.now();
        couponService.useCoupon(couponId, userId, useDate);

        return ResponseEntity.ok().build();
    }
}
