package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.exception.CouponNotFoundException;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponReader {
    private final CouponRepository couponRepository;

    public Coupon read(Long couponId) {
        return couponRepository.findById(couponId).orElseThrow(CouponNotFoundException::new);
    }
}
