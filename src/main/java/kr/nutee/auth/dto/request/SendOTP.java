package kr.nutee.auth.dto.request;

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
