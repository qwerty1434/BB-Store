package kr.bb.store.domain.store.exception;

import kr.bb.store.exception.CustomException;

public class InvalidDeliveryPriceException extends CustomException {

    private static final String MESSAGE = "올바른 배송금액이 아닙니다.";

    public InvalidDeliveryPriceException() {
        super(MESSAGE);
    }
}
