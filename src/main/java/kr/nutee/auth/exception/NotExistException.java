package kr.nutee.auth.exception;


import kr.nutee.auth.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotExistException extends BusinessException {
    public NotExistException(String msg, ErrorCode errorCode, HttpStatus status) {
        super(msg,errorCode,status);
    }
    public NotExistException(String msg, ErrorCode errorCode, HttpStatus status, Long postId) {
        super(msg,errorCode,status,postId);
    }
}
