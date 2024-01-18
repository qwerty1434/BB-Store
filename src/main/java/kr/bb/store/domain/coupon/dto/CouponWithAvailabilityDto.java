package kr.bb.store.domain.coupon.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CouponWithAvailabilityDto extends CouponDto {
    private Boolean isAvailable;

    @QueryProjection
    public CouponWithAvailabilityDto(Long couponId, String couponName, String storeName, Long discountPrice, LocalDate endDate, Long minPrice, Boolean isAvailable) {
        super(couponId, couponName, storeName, discountPrice, endDate, minPrice);
        this.isAvailable = isAvailable;
    }
}
