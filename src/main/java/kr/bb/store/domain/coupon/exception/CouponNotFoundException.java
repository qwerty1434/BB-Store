package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.advice.CustomException;

public class CouponNotFoundException extends CustomException {
    private static final String MESSAGE = "해당 쿠폰이 존재하지 않습니다.";

    public CouponNotFoundException() {
        super(MESSAGE);
    }
}
