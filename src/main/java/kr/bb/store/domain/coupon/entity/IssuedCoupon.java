package kr.bb.store.domain.coupon.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class IssuedCoupon {
    @EmbeddedId
    private IssuedCouponId id;

    @MapsId("couponId")
    @ManyToOne
    @JoinColumn(name="coupon_id")
    private Coupon coupon;

    private boolean isUsed;
}
