package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.handler.dto.CouponDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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

    public void use(IssuedCoupon issuedCoupon, LocalDate useDate) {
        issuedCoupon.use(useDate);
    }

    public void unUse(IssuedCoupon issuedCoupon) {
        issuedCoupon.unUse();
    }

    public void softDelete(Coupon coupon) {
        coupon.softDelete();
    }


}
