package kr.bb.store.domain.coupon.controller.response;

import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponsForOwnerResponse {
    private List<CouponForOwnerDto> data;
}
