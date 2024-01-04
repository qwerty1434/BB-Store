package kr.bb.store.domain.coupon.dto;

import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssuedCouponDto {
    private String nickname;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Boolean isUsed;

    public static IssuedCouponDto fromEntity(IssuedCoupon issuedCoupon) {
        return IssuedCouponDto.builder()
                .nickname(issuedCoupon.getNickname())
                .phoneNumber(issuedCoupon.getPhoneNumber())
                .createdAt(issuedCoupon.getCreatedAt())
                .isUsed(issuedCoupon.getIsUsed())
                .build();
    }
}
