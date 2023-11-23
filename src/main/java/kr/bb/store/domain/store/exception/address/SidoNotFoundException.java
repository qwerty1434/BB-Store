package kr.bb.store.domain.store.exception.address;

import kr.bb.store.exception.advice.CustomException;

public class SidoNotFoundException extends CustomException {
    public static final String MESSAGE = "해당 시/도가 존재하지 않습니다.";
    public SidoNotFoundException(){
        super(MESSAGE);
    }
}
