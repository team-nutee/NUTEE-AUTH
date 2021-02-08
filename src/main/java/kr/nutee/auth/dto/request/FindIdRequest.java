package kr.nutee.auth.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindIdRequest {
    private String schoolEmail;
}
