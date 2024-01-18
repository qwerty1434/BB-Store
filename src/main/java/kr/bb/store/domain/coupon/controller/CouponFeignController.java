package kr.bb.store.domain.coupon.controller;

import bloomingblooms.response.CommonResponse;
import kr.bb.store.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping(("/client/stores/coupons"))
public class CouponFeignController {
    private final CouponService couponService;

    @PostMapping("/{couponId}/users/{userId}")
    public void useCoupon(@PathVariable Long couponId, @PathVariable Long userId) {
        LocalDate useDate = LocalDate.now();
        couponService.useCoupon(couponId, userId, useDate);
    }

    @GetMapping("/count")
    public CommonResponse<Integer> availableCouponCountOfUser(@RequestHeader(value = "userId") Long userId) {
        LocalDate now = LocalDate.now();
        return CommonResponse.success(couponService.getMyAvailableCouponCount(userId, now));
    }
}
