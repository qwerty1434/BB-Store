package kr.bb.store.domain.coupon.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUseRequest {
    private Long couponId;
    private Long userId;
}
