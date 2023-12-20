package kr.bb.store.domain.coupon.controller;

import kr.bb.store.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping(("/client/stores/coupons"))
public class CouponFeignController {
    private final CouponService couponService;

    @PostMapping("/{couponId}/users/{userId}")
    public ResponseEntity<Void> useCoupon(@PathVariable Long couponId, @PathVariable Long userId) {
        LocalDate useDate = LocalDate.now();
        couponService.useCoupon(couponId, userId, useDate);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> availableCouponCountOfUser(@RequestHeader(value = "userId") Long userId) {
        LocalDate now = LocalDate.now();

        return ResponseEntity.ok().body(couponService.getMyAvailableCouponCount(userId, now));
    }
}
