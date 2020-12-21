package kr.nutee.auth.DTO.Response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class UserData implements Serializable {

    private Long id;

    private String nickname;

    private ImageResponse image;

    private List<String> interests;

    private List<String> majors;

    private int postNum;

    private int commentNum;

    private int likeNum;


    public UserData(Long id, String nickname, ImageResponse image, List<String> interests, List<String> majors, int postNum, int commentNum, int likeNum) {
        this.id = id;
        this.nickname = nickname;
        this.image = image;
        this.interests = interests;
        this.majors = majors;
        this.postNum = postNum;
        this.commentNum = commentNum;
        this.likeNum = likeNum;
    }
}
