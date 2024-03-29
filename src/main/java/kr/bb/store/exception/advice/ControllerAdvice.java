package kr.bb.store.exception.advice;

import bloomingblooms.response.CommonResponse;
import kr.bb.store.exception.CustomException;
import kr.bb.store.exception.NonPropagatingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(NonPropagatingException.class)
    public void nonPropagatingException(NonPropagatingException e) {
        log.warn("this error [{}] will not be thrown to user. rest logic will work well", e.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public CommonResponse customException(CustomException e) {
        log.error(e.getMessage());
        return CommonResponse.fail(e.getMessage(), "CE-01");
    }

}
