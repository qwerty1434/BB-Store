package kr.bb.store.domain.coupon.exception;

import kr.bb.store.exception.advice.CustomException;

public class DeletedCouponException extends CustomException {
    private final static String MESSAGE = "해당 쿠폰은 관리자에 의해 삭제되었습니다.";

    public DeletedCouponException() {
        super(MESSAGE);
    }
}
