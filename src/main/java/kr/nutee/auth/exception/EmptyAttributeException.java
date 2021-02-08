package kr.nutee.auth.exception;

import kr.nutee.auth.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class EmptyAttributeException extends BusinessException {
    public EmptyAttributeException(String msg, ErrorCode errorCode, HttpStatus status) {
        super(msg,errorCode,status);
    }
}
