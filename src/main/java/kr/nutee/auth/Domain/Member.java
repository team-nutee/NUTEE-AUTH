package kr.nutee.auth.Domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import kr.nutee.auth.Enum.RoleType;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Member extends LogDateTime {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable=false, unique=true, length=20)
    private String userId;

    @Column(nullable=false, unique=true, length=20)
    private String nickname;

    @Column(nullable=false, length=50)
    private String schoolEmail;

    private String password;

    private LocalDateTime accessedAt;

    @OneToOne (mappedBy = "member")
    private Image image;

    @JsonManagedReference
    @OneToMany (mappedBy = "member")
    @Builder.Default
    private List<Interest> interests = new ArrayList<>();

    @JsonManagedReference
    @OneToMany (mappedBy = "member")
    @Builder.Default
    private List<Major> majors = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private RoleType role;

    private boolean isDeleted;

    private boolean isBlocked;

}
