package kr.nutee.auth.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class AdviceController {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> duplicateEx(Exception e) {
        log.warn("DataIntegrityViolationException" + e.getClass());
        return new ResponseEntity<>("Unique 키가 데이터베이스 내부 에서 중복", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Map<String, Object> badCredentialEx(Exception e) {
        log.warn("BadCredentialsException" + e.getClass());
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", 63);
        return map;
    }

    @ExceptionHandler({
            IllegalArgumentException.class, MissingServletRequestParameterException.class})
    public Map<String, Object> paramsEx(Exception e) {
        log.warn("params ex: "+ e.getClass());
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", 51);
        return map;
    }

    @ExceptionHandler(NullPointerException.class)
    public Map<String, Object> nullEx(Exception e) {
        log.warn("null ex" + e.getClass());
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", 61);
        return map;
    }

}
