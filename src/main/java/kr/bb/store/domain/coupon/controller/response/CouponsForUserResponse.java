package kr.bb.store.domain.coupon.controller.response;

import kr.bb.store.domain.coupon.dto.CouponDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponsForUserResponse {
    private List<? extends CouponDto> data;

    public static CouponsForUserResponse from(List<? extends CouponDto> couponDtos) {
        return CouponsForUserResponse.builder()
                .data(couponDtos)
                .build();
    }
}
