package kr.bb.store.domain.coupon.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
public class CouponForOwnerDto {
    private Long key;
    private String couponCode;
    private String couponName;
    private Long minPrice;
    private Long discountPrice;
    private Integer unusedCount;
    private LocalDate startDate;
    private LocalDate endDate;

    @QueryProjection
    public CouponForOwnerDto(Long key, String couponCode, String couponName, Long minPrice, Long discountPrice, Integer unusedCount, LocalDate startDate, LocalDate endDate) {
        this.key = key;
        this.couponCode = couponCode;
        this.couponName = couponName;
        this.minPrice = minPrice;
        this.discountPrice = discountPrice;
        this.unusedCount = unusedCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
