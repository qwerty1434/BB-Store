package kr.bb.store.domain.store.exception;

import kr.bb.store.exception.advice.CustomException;

public class CannotOwnMultipleStoreException extends CustomException {
    public static final String MESSAGE = "둘 이상의 가게를 생성할 수 없습니다.";

    public CannotOwnMultipleStoreException() {
        super(MESSAGE);
    }
}
