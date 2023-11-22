package kr.bb.store.domain.store.exception;

import kr.bb.store.exception.advice.CustomException;

public class StoreNotFoundException extends CustomException {
    public static final String MESSAGE = "해당 가게가 존재하지 않습니다.";

    public StoreNotFoundException() {
        super(MESSAGE);
    }
}
