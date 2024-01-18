package kr.bb.store.domain.store.exception.address;

import kr.bb.store.exception.CustomException;

public class InvalidParentException extends CustomException {
    public static final String MESSAGE = "선택한 시/도와 구/군이 맞지 않습니다.";
    public InvalidParentException() {
        super(MESSAGE);
    }
}
