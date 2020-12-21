package kr.nutee.auth.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Response {
    int code;
    String message;
    Object body;
}
