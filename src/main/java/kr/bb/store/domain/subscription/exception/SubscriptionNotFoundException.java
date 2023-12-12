package kr.bb.store.domain.subscription.exception;

import kr.bb.store.exception.CustomException;

public class SubscriptionNotFoundException extends CustomException {
    private static final String MESSAGE = "구독정보가 존재하지 않습니다.";

    public SubscriptionNotFoundException() {
        super(MESSAGE);
    }
}
