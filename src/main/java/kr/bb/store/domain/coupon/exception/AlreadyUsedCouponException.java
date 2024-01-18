package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.CustomException;

public class AlreadyUsedCouponException extends CustomException {
    private static final String MESSAGE = "이미 사용한 쿠폰입니다.";

    public AlreadyUsedCouponException() {
        super(MESSAGE);
    }
}
