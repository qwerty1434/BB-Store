package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import kr.bb.store.domain.coupon.exception.CouponOutOfStockException;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class CouponIssuer {
    private final IssuedCouponRepository issuedCouponRepository;

    public IssuedCoupon issueCoupon(Coupon coupon, Long userId, LocalDate issueDate) {
        coupon.checkExpire(issueDate);
        if(ExhaustedCouponStock(coupon)) throw new CouponOutOfStockException();

        return issuedCouponRepository.save(makeIssuedCoupon(coupon,userId));
    }

    private IssuedCoupon makeIssuedCoupon(Coupon coupon, Long userId) {
        return IssuedCoupon.builder()
                .id(makeIssuedCouponId(coupon.getId(), userId))
                .coupon(coupon)
                .build();
    }

    private IssuedCouponId makeIssuedCouponId(Long couponId, Long userId) {
        return IssuedCouponId.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
    }

    private boolean ExhaustedCouponStock(Coupon coupon) {
        Long issuedCouponCount = issuedCouponRepository.findIssuedCountByCouponId(coupon.getId());
        return coupon.getLimitCount() - issuedCouponCount == 0;
    }

}
