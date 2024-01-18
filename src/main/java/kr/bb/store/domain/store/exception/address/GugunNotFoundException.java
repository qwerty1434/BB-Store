package kr.bb.store.domain.store.exception.address;

import kr.bb.store.exception.CustomException;

public class GugunNotFoundException extends CustomException {
    public static final String MESSAGE = "해당 구/군이 존재하지 않습니다.";
    public GugunNotFoundException() {
        super(MESSAGE);
    }
}
