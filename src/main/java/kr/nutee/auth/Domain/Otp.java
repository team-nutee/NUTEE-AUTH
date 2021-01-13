package kr.nutee.auth.Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import kr.nutee.auth.Enum.RoleType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "otps")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Otp {
    @Transient
    public static final String SEQUENCE_NAME = "otps_sequence";

    @Id
    private long id;

    private String otpNumber;

    private LocalDateTime createdAt;
}
