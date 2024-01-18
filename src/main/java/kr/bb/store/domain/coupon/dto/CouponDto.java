package kr.bb.store.domain.coupon.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
public class CouponDto {
    private Long couponId;
    private String couponName;
    private String storeName;
    private Long discountPrice;
    private LocalDate endDate;
    private Long minPrice;

    @QueryProjection
    public CouponDto(Long couponId, String couponName, String storeName, Long discountPrice, LocalDate endDate, Long minPrice) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.storeName = storeName;
        this.discountPrice = discountPrice;
        this.endDate = endDate;
        this.minPrice = minPrice;
    }
}
