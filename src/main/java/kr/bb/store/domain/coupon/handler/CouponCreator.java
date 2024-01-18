package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.handler.dto.CouponDto;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import kr.bb.store.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CouponCreator {
    private final CouponRepository couponRepository;

    public Coupon create(Store store, CouponDto couponDto) {

        Coupon coupon = Coupon.builder()
                .couponCode(UUID.randomUUID().toString().substring(0,8))
                .store(store)
                .limitCount(couponDto.getLimitCount())
                .couponName(couponDto.getCouponName())
                .discountPrice(couponDto.getDiscountPrice())
                .minPrice(couponDto.getMinPrice())
                .startDate(couponDto.getStartDate())
                .endDate(couponDto.getEndDate())
                .build();

        return couponRepository.save(coupon);

    }

}
