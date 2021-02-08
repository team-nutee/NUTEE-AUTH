package kr.nutee.auth.dto.messageQueue;

import kr.nutee.auth.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberMessage {
    String method;
    Member origin;
    Member change;
}
