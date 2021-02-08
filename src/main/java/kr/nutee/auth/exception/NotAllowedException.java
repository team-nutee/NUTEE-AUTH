package kr.nutee.auth.exception;

import kr.nutee.auth.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotAllowedException extends BusinessException {
    public NotAllowedException(String msg, ErrorCode errorCode, HttpStatus status) {
        super(msg,errorCode,status);
    }
    public NotAllowedException(String msg, ErrorCode errorCode, HttpStatus status, Long postId) {
        super(msg,errorCode,status,postId);
    }
}
