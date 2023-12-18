package kr.bb.store.domain.cargo.exception;

import kr.bb.store.exception.CustomException;

public class LockInterruptedException extends CustomException {
    private static final String MESSAGE = "락 획득 시도중 인터럽트가 발생했습니다.";

    public LockInterruptedException() {
        super(MESSAGE);
    }
}
