package kr.nutee.auth.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckNicknameDTO {
    private String nickname;
}
