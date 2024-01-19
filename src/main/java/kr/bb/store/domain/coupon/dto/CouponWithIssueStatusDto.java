package kr.bb.store.domain.coupon.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CouponWithIssueStatusDto extends CouponDto {
    private Boolean isIssued;
    private Integer limitCount;
    private String couponCode;

    @QueryProjection
    public CouponWithIssueStatusDto(Long couponId, String couponName, String storeName, Long discountPrice, LocalDate endDate, Long minPrice, Boolean isIssued, Integer limitCount, String couponCode) {
        super(couponId, couponName, storeName, discountPrice, endDate, minPrice);
        this.isIssued = isIssued;
        this.limitCount = limitCount;
        this.couponCode = couponCode;
    }
}
