package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.CustomException;

public class NotIssuedCouponException extends CustomException {
    private static final String MESSAGE = "발급된적 없는 쿠폰입니다.";

    public NotIssuedCouponException() {
        super(MESSAGE);
    }
}
