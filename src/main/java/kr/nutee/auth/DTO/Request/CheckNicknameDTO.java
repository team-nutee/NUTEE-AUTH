package kr.nutee.auth.DTO.Request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckNicknameDTO {
    private String nickname;
}
