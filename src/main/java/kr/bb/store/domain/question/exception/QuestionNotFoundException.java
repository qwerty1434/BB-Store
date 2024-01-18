package kr.bb.store.domain.question.exception;

public class QuestionNotFoundException extends RuntimeException {
    public static final String MESSAGE = "해당 문의를 찾을 수 없습니다.";

    public QuestionNotFoundException() {
        super(MESSAGE);
    }
}
