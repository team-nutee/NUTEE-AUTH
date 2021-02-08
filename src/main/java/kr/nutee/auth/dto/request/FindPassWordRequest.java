package kr.nutee.auth.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindPassWordRequest {
    private String schoolEmail;
    private String userId;
}
