package kr.nutee.auth.DTO.Request;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {
    private String accessToken;
    private String refreshToken;
}
