package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.CustomException;

public class InvalidDiscountPriceException extends CustomException {
    private static final String MESSAGE = "올바른 할인금액이 아닙니다.";

    public InvalidDiscountPriceException() {
        super(MESSAGE);
    }
}
