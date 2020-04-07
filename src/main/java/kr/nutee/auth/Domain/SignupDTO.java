package kr.nutee.auth.Domain;

import lombok.*;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class SignupDTO {
    private Long id;
    private String userId;
    private String nickname;
    private String schoolEmail;
    private String password;
    private String otp;
}