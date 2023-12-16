package kr.bb.store.domain.cargo.exception;

import kr.bb.store.exception.CustomException;

public class StockCannotBeNegativeException extends CustomException {
    private static final String MESSAGE = "재고는 음수가 될 수 없습니다.";

    public StockCannotBeNegativeException() {
        super(MESSAGE);
    }
}
