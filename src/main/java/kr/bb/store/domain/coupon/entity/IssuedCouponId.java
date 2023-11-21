package kr.bb.store.domain.coupon.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class IssuedCouponId implements Serializable {
    private Long couponId;
    private Long userId;
}
