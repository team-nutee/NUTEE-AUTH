package kr.nutee.auth.Exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException {
    public ConflictException(String msg, HttpStatus status) {
        super(msg,status);
    }
}
