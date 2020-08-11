package kr.nutee.auth.DTO.Request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckEmailDTO {
    private String schoolEmail;
}
