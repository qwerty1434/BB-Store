package kr.bb.store.domain.store.exception;

import kr.bb.store.exception.CustomException;

public class DeliveryInconsistencyException extends CustomException {

    private static final String MESSAGE = "주문 요청이 배송 정책을 위반했습니다.";

    public DeliveryInconsistencyException() {
        super(MESSAGE);
    }
}
