package kr.bb.store.domain.coupon.exception;

public class ExpiredCouponException extends RuntimeException {
    private static final String MESSAGE = "기한이 만료된 쿠폰입니다.";

    public ExpiredCouponException() {
        super(MESSAGE);
    }
}
