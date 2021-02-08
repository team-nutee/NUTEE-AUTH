package kr.nutee.auth.domain;

import kr.nutee.auth.enums.RoleType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "members")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Transient
    public static final String SEQUENCE_NAME = "members_sequence";

    @Id
    private long id;

    private String userId;

    private String nickname;

    private String schoolEmail;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime accessedAt;

    private String profileUrl;

    @Builder.Default
    private List<String> interests = new ArrayList<>();

    @Builder.Default
    private List<String> majors = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private RoleType role;

    private boolean isDeleted;

    private boolean isBlocked;

}
