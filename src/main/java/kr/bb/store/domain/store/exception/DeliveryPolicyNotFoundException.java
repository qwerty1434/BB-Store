package kr.bb.store.domain.store.exception;

import kr.bb.store.exception.CustomException;

public class DeliveryPolicyNotFoundException extends CustomException {
    public static final String MESSAGE = "해당 가게의 배송정책이 존재하지 않습니다.";

    public DeliveryPolicyNotFoundException() {
        super(MESSAGE);
    }
}
