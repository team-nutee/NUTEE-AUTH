package kr.nutee.auth.Exception;

import kr.nutee.auth.Enum.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConflictException extends BusinessException {
    public ConflictException(String msg, ErrorCode errorCode, HttpStatus status) {
        super(msg,errorCode,status);
    }
    public ConflictException(String msg, ErrorCode errorCode, HttpStatus status, Long postId) {
        super(msg,errorCode,status,postId);
    }
}
