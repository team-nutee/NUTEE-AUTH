package kr.nutee.auth.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Long memberId;
    private String accessToken;
    private String refreshToken;
}
