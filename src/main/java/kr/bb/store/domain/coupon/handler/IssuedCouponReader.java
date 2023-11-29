package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import kr.bb.store.domain.coupon.exception.NotIssuedCouponException;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class IssuedCouponReader {
    private final IssuedCouponRepository issuedCouponRepository;

    public IssuedCoupon read(Long couponId, Long userId) {
        IssuedCouponId id = makeIssuedCouponId(couponId, userId);
        return issuedCouponRepository.findById(id).orElseThrow(NotIssuedCouponException::new);
    }

    private IssuedCouponId makeIssuedCouponId(Long couponId, Long userId) {
        return IssuedCouponId.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
    }
}
