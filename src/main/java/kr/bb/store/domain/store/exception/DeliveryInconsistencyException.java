package kr.bb.store.domain.store.exception;

import kr.bb.store.exception.CustomException;

public class DeliveryInconsistencyException extends CustomException {

    private static final String MESSAGE = "해당 요청은 실제 배송 정보와 일치하지 않습니다.";

    public DeliveryInconsistencyException() {
        super(MESSAGE);
    }
}
