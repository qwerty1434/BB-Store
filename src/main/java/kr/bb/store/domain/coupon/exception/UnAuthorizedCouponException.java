package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.CustomException;

public class UnAuthorizedCouponException extends CustomException {
    private static final String MESSAGE = "해당 쿠폰에 대한 권한이 없습니다.";

    public UnAuthorizedCouponException() {
        super(MESSAGE);
    }
}
