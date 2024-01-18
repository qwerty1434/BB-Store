package kr.bb.store.domain.cargo.exception;

import kr.bb.store.exception.CustomException;

public class FlowerCargoNotFoundException extends CustomException {
    public static final String MESSAGE = "해당 가게는 해당 꽃 재고를 등록하지 않았습니다";

    public FlowerCargoNotFoundException() {
        super(MESSAGE);
    }
}
