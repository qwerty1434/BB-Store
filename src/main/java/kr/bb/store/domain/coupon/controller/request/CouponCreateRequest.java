package kr.bb.store.domain.coupon.controller.request;

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
}
