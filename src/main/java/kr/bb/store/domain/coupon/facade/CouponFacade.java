package kr.bb.store.domain.coupon.facade;

import bloomingblooms.domain.order.ProcessOrderDto;
import kr.bb.store.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponService couponService;

    @KafkaListener(topics = "coupon-use", groupId = "use-coupon")
    public void useCoupons(ProcessOrderDto processOrderDto) {
        LocalDate useDate = LocalDate.now();
        couponService.useAllCoupons(processOrderDto.getCouponIds(), processOrderDto.getUserId(), useDate);
    }
}
