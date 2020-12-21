package kr.nutee.auth.DTO;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Token implements Serializable {

    private static final long serialVersionUID = -7353484588260422449L;
    private String userId;
    private String accessToken;
    private String refreshToken;

}
