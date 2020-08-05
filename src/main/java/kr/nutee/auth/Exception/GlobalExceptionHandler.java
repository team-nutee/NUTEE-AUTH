package kr.nutee.auth.Exception;

import kr.nutee.auth.Controller.AuthController;
import kr.nutee.auth.DTO.Resource.ResponseResource;
import kr.nutee.auth.DTO.Response.Response;
import kr.nutee.auth.Enum.ErrorCode;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<ResponseResource> notAllowedException(BusinessException e) {
        Response response = Response.builder()
                .code(21)
                .message(e.getMessage())
                .body(null)
                .build();

        ResponseResource resource = getResponseResource(e, response);

        log.warn("NotAllowedException" + e.getClass());
        return ResponseEntity.status(e.getStatus()).body(resource);
    }

    @ExceptionHandler(NotExistException.class)
    public ResponseEntity<ResponseResource> notExistException(BusinessException e) {
        Response response = Response.builder()
                .code(22)
                .message(e.getMessage())
                .body(null)
                .build();

        ResponseResource resource = getResponseResource(e, response);

        log.warn("NotExistException" + e.getClass());
        return ResponseEntity.status(e.getStatus()).body(resource);
    }

    @ExceptionHandler(EmptyAttributeException.class)
    public ResponseEntity<Response> emptyAttributeException(BusinessException e) {
        Response res = Response.builder()
                .code(23)
                .message(e.getMessage())
                .body(null)
                .build();
        log.warn("EmptyAttributeException" + e.getClass());
        return new ResponseEntity<>(res, e.getStatus());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Response> conflictException(BusinessException e) {
        Response res = Response.builder()
                .code(23)
                .message(e.getMessage())
                .body(null)
                .build();
        log.warn("ConflictException" + e.getClass());
        return new ResponseEntity<>(res, e.getStatus());
    }



    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> duplicateEx(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 51);
        map.put("message","Unique 키가 데이터베이스 내부 에서 중복");
        log.warn("DataIntegrityViolationException" + e.getClass());
        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            IllegalArgumentException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Object> paramsEx(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 52);
        map.put("message","");
        log.warn("params ex: "+ e.getClass());
        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

//    @ExceptionHandler(NullPointerException.class)
//    public ResponseEntity<Object> nullEx(Exception e) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", 53);
//        map.put("message","");
//        log.warn("null ex" + e.getClass());
//        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
//    }

    @ExceptionHandler(BadCredentialsException.class)
    public Map<String, Object> badCredentialEx(Exception e) {
        log.warn("BadCredentialsException" + e.getClass());
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", 63);
        return map;
    }

    private ResponseResource getResponseResource(BusinessException e, Response response) {
        ResponseResource resource = new ResponseResource(response, AuthController.class, e.getPostId());
        if(e.getErrorCode() != ErrorCode.NOT_EXIST){
            resource.add(linkTo(AuthController.class).slash(e.getPostId()).withRel("update-post"));
            resource.add(linkTo(AuthController.class).slash(e.getPostId()).withRel("remove-post"));
        }
        resource.add(linkTo(AuthController.class).slash("favorite").withRel("get-favorite-posts"));
//        resource.add(linkTo(AuthController.class).slash(postRepository.findPostById(e.getPostId()).getCategory()).withRel("get-category-posts"));
        return resource;
    }
}
