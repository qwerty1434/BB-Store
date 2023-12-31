package kr.bb.store.domain.coupon.facade;

import bloomingblooms.domain.notification.NotificationKind;
import bloomingblooms.domain.order.ProcessOrderDto;
import kr.bb.store.domain.coupon.service.CouponService;
import kr.bb.store.util.KafkaProcessor;
import kr.bb.store.message.OrderStatusSQSPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponService couponService;
    private final OrderStatusSQSPublisher orderStatusSQSPublisher;
    private final KafkaProcessor<ProcessOrderDto> stockDecreaseKafkaProducer;

    @KafkaListener(topics = "coupon-use", groupId = "use-coupon")
    public void useCoupons(ProcessOrderDto processOrderDto) {
        try {
            LocalDate useDate = LocalDate.now();
            couponService.useAllCoupons(processOrderDto.getCouponIds(), processOrderDto.getUserId(), useDate);
            log.info("coupon from order {} used successfully", processOrderDto.getOrderId());
            stockDecreaseKafkaProducer.send("stock-decrease", processOrderDto);
        } catch (Exception e) {
            Long userId = processOrderDto.getUserId();
            String phoneNumber = processOrderDto.getPhoneNumber();
            orderStatusSQSPublisher.publish(userId, phoneNumber, NotificationKind.INVALID_COUPON);
            throw e;
        }
    }

    @KafkaListener(topics = "stock-decrease-rollback", groupId = "rollback-coupon")
    public void rollbackCoupons(ProcessOrderDto processOrderDto) {
        couponService.unUseAllCoupons(processOrderDto.getCouponIds(), processOrderDto.getUserId());
        log.info("coupon from order {} rollbacked successfully", processOrderDto.getOrderId());
    }
}
