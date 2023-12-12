package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.CustomException;

public class InvalidCouponStartDateException extends CustomException {
    private static final String MESSAGE = "시작일이 올바르지 않습니다.";

    public InvalidCouponStartDateException() {
        super(MESSAGE);
    }
}
