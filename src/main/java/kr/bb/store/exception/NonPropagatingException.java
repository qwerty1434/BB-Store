package kr.bb.store.exception;

public class NonPropagatingException extends CustomException {

    public NonPropagatingException(String message) {
        super(message);
    }
}
