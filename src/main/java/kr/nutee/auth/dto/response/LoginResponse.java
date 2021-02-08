package kr.nutee.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Long memberId;
    private String accessToken;
    private String refreshToken;
}
