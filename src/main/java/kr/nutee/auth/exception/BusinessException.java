package kr.nutee.auth.exception;


import kr.nutee.auth.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    ErrorCode errorCode;
    HttpStatus status;
    Long postId;

    public BusinessException(String msg, ErrorCode errorCode, HttpStatus status) {
        super(msg);
        this.errorCode = errorCode;
        this.status = status;
    }

    public BusinessException(String msg, ErrorCode errorCode, HttpStatus status, Long postId) {
        super(msg);
        this.errorCode = errorCode;
        this.status = status;
        this.postId = postId;
    }
}
