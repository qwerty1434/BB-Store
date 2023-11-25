package kr.bb.store.domain.coupon.entity;


import kr.bb.store.domain.common.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IssuedCoupon extends BaseEntity {
    @EmbeddedId
    private IssuedCouponId id;

    @MapsId("couponId")
    @ManyToOne
    @JoinColumn(name="coupon_id")
    private Coupon coupon;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isUsed;
}
