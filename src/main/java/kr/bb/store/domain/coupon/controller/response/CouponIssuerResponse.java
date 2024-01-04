package kr.bb.store.domain.coupon.controller.response;

import kr.bb.store.domain.coupon.dto.IssuedCouponDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssuerResponse {
    private List<IssuedCouponDto> data;
    private Long totalCnt;

    public static CouponIssuerResponse of(List<IssuedCouponDto> data, Long totalCnt) {
        return CouponIssuerResponse.builder()
                .data(data)
                .totalCnt(totalCnt)
                .build();
    }
}
