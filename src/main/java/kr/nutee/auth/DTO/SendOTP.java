package kr.nutee.auth.DTO;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendOTP {
    @NotEmpty
    private String schoolEmail;
}
