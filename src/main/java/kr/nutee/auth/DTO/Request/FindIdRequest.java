package kr.nutee.auth.DTO.Request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindIdRequest {
    private String schoolEmail;
}
