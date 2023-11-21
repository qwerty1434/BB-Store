package kr.bb.store.domain.coupon.entity;


import kr.bb.store.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class IssuedCoupon extends BaseEntity {
    @EmbeddedId
    private IssuedCouponId id;

    @MapsId("couponId")
    @ManyToOne
    @JoinColumn(name="coupon_id")
    private Coupon coupon;

    @NotNull
    private boolean isUsed;
}
