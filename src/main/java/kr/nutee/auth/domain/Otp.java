package kr.nutee.auth.domain;

import java.time.LocalDateTime;
import javax.persistence.Transient;
import lombok.*;

import javax.persistence.Id;
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
