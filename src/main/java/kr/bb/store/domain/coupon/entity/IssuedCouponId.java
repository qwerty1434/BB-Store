package kr.bb.store.domain.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class IssuedCouponId implements Serializable {
    private Long couponId;
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssuedCouponId that = (IssuedCouponId) o;
        return Objects.equals(couponId, that.couponId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(couponId, userId);
    }
}
