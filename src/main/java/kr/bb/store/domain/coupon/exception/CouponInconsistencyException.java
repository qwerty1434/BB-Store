package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.CustomException;

public class CouponInconsistencyException extends CustomException {
    private static final String MESSAGE = "해당 요청은 실제 쿠폰 정보와 일치하지 않습니다.";

    public CouponInconsistencyException() {
        super(MESSAGE);
    }
}
