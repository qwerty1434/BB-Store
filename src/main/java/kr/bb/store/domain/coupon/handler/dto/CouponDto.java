package kr.bb.store.domain.coupon.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {
    private String couponName;
    private Long discountPrice;
    private Long minPrice;
    private Integer limitCount;
    private LocalDate startDate;
    private LocalDate endDate;
}
