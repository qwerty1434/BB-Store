package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.handler.dto.CouponDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponManager {
    public void edit(Coupon coupon, CouponDto couponEditDto) {
        coupon.update(
                couponEditDto.getLimitCount(),
                couponEditDto.getCouponName(),
                couponEditDto.getDiscountPrice(),
                couponEditDto.getMinPrice(),
                couponEditDto.getStartDate(),
                couponEditDto.getEndDate()
        );
    }
}
