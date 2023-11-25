package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.exception.InvalidCouponDurationException;
import kr.bb.store.domain.coupon.exception.InvalidCouponStartDateException;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CouponCreator {
    private final CouponRepository couponRepository;

    public Coupon create(Long storeId, Integer limitCount, String couponName, Long discountPrice, Long minPrice,
                         LocalDate startDate, LocalDate endDate) {
        dateValidationCheck(startDate,endDate);

        Coupon coupon = Coupon.builder()
                .couponCode(UUID.randomUUID().toString())
                .storeId(storeId)
                .limitCount(limitCount)
                .couponName(couponName)
                .discountPrice(discountPrice)
                .minPrice(minPrice)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return couponRepository.save(coupon);
    }

    private void dateValidationCheck(LocalDate startDate, LocalDate endDate) {
        if(startDate.isBefore(LocalDate.now())) throw new InvalidCouponStartDateException();
        if(endDate.isBefore(startDate)) throw new InvalidCouponDurationException();

    }
}
