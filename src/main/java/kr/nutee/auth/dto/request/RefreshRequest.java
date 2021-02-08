package kr.nutee.auth.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {
    private String accessToken;
    private String refreshToken;
}
