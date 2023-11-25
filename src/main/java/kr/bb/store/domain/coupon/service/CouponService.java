package kr.bb.store.domain.coupon.service;

import kr.bb.store.domain.coupon.controller.request.CouponCreateRequest;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.handler.CouponCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {
    private final CouponCreator couponCreator;
    public Coupon createCoupon(Long storeId, CouponCreateRequest request) {
        return couponCreator.create(storeId, request.getLimitCount(), request.getCouponName(),
                request.getDiscountPrice(),request.getMinPrice(), request.getStartDate(), request.getEndDate());
    }
}
