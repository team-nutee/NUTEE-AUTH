package kr.nutee.auth.DTO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Response {
    int code;
    String message;
    Object body;
}
