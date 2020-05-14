package kr.nutee.auth.Entity;

import kr.nutee.auth.Enum.RoleType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Member extends LogDateTime {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=20)
    private String userId;

    @Column(nullable=false, unique=true, length=20)
    private String nickname;

    @Column(nullable=false, length=50)
    private String schoolEmail;

    private String password;

    private LocalDateTime accessedAt;

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private RoleType role;

    private boolean isDeleted;

    private boolean isBlocked;

}
