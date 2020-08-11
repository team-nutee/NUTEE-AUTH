package kr.nutee.auth.Exception;

import kr.nutee.auth.Enum.ErrorCode;
import org.springframework.http.HttpStatus;

public class EmptyAttributeException extends BusinessException {
    public EmptyAttributeException(String msg, ErrorCode errorCode, HttpStatus status) {
        super(msg,errorCode,status);
    }
}
