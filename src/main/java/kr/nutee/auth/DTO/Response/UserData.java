package kr.nutee.auth.DTO.Response;

import kr.nutee.auth.Domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserData implements Serializable {

    private Long id;

    private String nickname;

    private String profileUrl;

    private List<String> interests;

    private List<String> majors;


    public UserData(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.profileUrl = member.getProfileUrl();
        this.interests = member.getInterests();
        this.majors = member.getMajors();
    }
}
