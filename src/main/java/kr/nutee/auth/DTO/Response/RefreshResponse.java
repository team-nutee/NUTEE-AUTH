package kr.nutee.auth.DTO.Response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponse {
    private String accessToken;
}
