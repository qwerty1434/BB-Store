package kr.bb.store.domain.coupon.entity;


import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.coupon.exception.AlreadyUsedCouponException;
import kr.bb.store.domain.coupon.exception.ExpiredCouponException;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDate;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="coupon_id")
    private Coupon coupon;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isUsed = false;

    public void use(LocalDate now) {
        if(isUsed) throw new AlreadyUsedCouponException();
        if(coupon.isExpired(now)) throw new ExpiredCouponException();
        isUsed = true;
    }
}
