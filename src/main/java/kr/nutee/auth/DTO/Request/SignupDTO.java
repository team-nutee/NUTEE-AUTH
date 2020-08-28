package kr.nutee.auth.DTO.Request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class SignupDTO {

    @NotEmpty
    private String userId;

    @NotEmpty
    private String nickname;

    @NotEmpty
    private String schoolEmail;

    @NotEmpty
    private String password;

    @NotEmpty
    private String otp;

    @NotEmpty
    private List<String> interests;

    @NotEmpty
    private List<String> majors;
}