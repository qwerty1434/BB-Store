package kr.bb.store.domain.coupon.controller;

import kr.bb.store.domain.coupon.controller.request.UserInfoRequest;
import kr.bb.store.domain.coupon.facade.CouponFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final CouponFacade couponFacade;

    @PostMapping("/test/coupons/{couponId}")
    public void downloadCoupon(@PathVariable Long couponId,
                               @RequestHeader(value = "userId") Long userId,
                               @RequestBody UserInfoRequest userInfoRequest) {
        couponFacade.downloadCoupon(userId, couponId, userInfoRequest.getNickname(), userInfoRequest.getPhoneNumber(), LocalDate.now());
    }

}
