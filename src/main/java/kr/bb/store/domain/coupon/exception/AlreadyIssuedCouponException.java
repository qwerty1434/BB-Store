package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.advice.CustomException;

public class AlreadyIssuedCouponException extends CustomException {
    private static final String MESSAGE = "이미 발급받은 쿠폰입니다.";

    public AlreadyIssuedCouponException() {
        super(MESSAGE);
    }
}
