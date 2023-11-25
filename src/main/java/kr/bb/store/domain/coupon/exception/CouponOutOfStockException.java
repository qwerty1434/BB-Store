package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.advice.CustomException;

public class CouponOutOfStockException extends CustomException {
    private static final String MESSAGE = "준비된 쿠폰이 모두 소진되었습니다.";

    public CouponOutOfStockException() {
        super(MESSAGE);
    }
}
