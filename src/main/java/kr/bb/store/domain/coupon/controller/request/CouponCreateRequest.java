package kr.bb.store.domain.coupon.controller.request;

import kr.bb.store.domain.coupon.handler.dto.CouponDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponCreateRequest {
    private String couponName;
    private Long discountPrice;
    private Long minPrice;
    private Integer limitCount;
    private LocalDate startDate;
    private LocalDate endDate;

    public CouponDto toDto() {
        return CouponDto.builder()
                .couponName(couponName)
                .discountPrice(discountPrice)
                .minPrice(minPrice)
                .limitCount(limitCount)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
