package kr.bb.store.domain.cargo.exception;

import kr.bb.store.exception.CustomException;

public class StockChangeFailedException extends CustomException {
    public static final String MESSAGE = "재고차감 변경에 실패했습니다.";

    public StockChangeFailedException() {
        super(MESSAGE);
    }
}
