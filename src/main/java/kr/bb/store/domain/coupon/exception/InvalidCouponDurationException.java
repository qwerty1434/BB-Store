package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.CustomException;

public class InvalidCouponDurationException extends CustomException {
    private static final String MESSAGE = "시작일과 종료일이 올바르지 않습니다.";

    public InvalidCouponDurationException() {
        super(MESSAGE);
    }
}
