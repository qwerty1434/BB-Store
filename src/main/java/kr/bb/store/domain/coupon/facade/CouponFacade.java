package kr.bb.store.domain.coupon.facade;

import bloomingblooms.domain.notification.NotificationKind;
import bloomingblooms.domain.order.ProcessOrderDto;
import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.controller.request.TotalAmountRequest;
import kr.bb.store.domain.coupon.controller.response.CouponIssuerResponse;
import kr.bb.store.domain.coupon.controller.response.CouponsForOwnerResponse;
import kr.bb.store.domain.coupon.controller.response.CouponsForUserResponse;
import kr.bb.store.domain.coupon.service.CouponService;
import kr.bb.store.message.OrderStatusSQSPublisher;
import kr.bb.store.util.KafkaProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
            log.error("coupon use failed with cause of {}", e);
        }
    }

    @KafkaListener(topics = "stock-decrease-rollback", groupId = "rollback-coupon")
    public void rollbackCoupons(ProcessOrderDto processOrderDto) {
        couponService.unUseAllCoupons(processOrderDto.getCouponIds(), processOrderDto.getUserId());
        log.info("coupon from order {} rollbacked successfully", processOrderDto.getOrderId());
    }

    public void createCoupon(Long storeId, CouponCreateRequest couponCreateRequest) {
        couponService.createCoupon(storeId, couponCreateRequest);
    }

    public void editCoupon(Long storeId, Long couponId, CouponEditRequest couponEditRequest) {
        couponService.editCoupon(storeId, couponId, couponEditRequest);
    }

    public void softDeleteCoupon(Long storeId, Long couponId) {
        couponService.softDeleteCoupon(storeId, couponId);
    }

    public CouponsForOwnerResponse getAllStoreCoupons(Long storeId) {
        return CouponsForOwnerResponse.from(couponService.getAllStoreCoupons(storeId));
    }

    public void downloadCoupon(Long userId, Long couponId, String nickname, String phoneNumber, LocalDate now) {
        couponService.downloadCoupon(userId, couponId, nickname, phoneNumber, now);
    }

    public void downloadAllCoupons(Long userId, Long storeId, String nickname, String phoneNumber, LocalDate now) {
        couponService.downloadAllCoupons(userId, storeId, nickname, phoneNumber, now);
    }

    public CouponsForUserResponse getAllStoreCouponsForUser(Long userId, Long storeId, LocalDate now) {
        return CouponsForUserResponse.from(couponService.getAllStoreCouponsForUser(userId, storeId, now));
    }

    public CouponsForUserResponse getAvailableCouponsInPayment(TotalAmountRequest totalAmountRequest,
            Long userId, Long storeId, LocalDate now) {
        return CouponsForUserResponse.from(couponService.getAvailableCouponsInPayment(totalAmountRequest, userId, storeId, now));
    }

    public CouponsForUserResponse getMyValidCoupons(Long userId, LocalDate now) {
        return CouponsForUserResponse.from(couponService.getMyValidCoupons(userId, now));
    }

    public CouponIssuerResponse getCouponMembers(Long userId, Long couponId, Pageable pageable) {
        return couponService.getCouponMembers(userId, couponId, pageable);
    }

}