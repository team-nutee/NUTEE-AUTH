package kr.nutee.auth.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import kr.nutee.auth.Domain.Otp;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OtpRepository extends MongoRepository<Otp,String> {
    Otp findOtpByOtpNumber(String otp);
    void deleteOtpByOtpNumber(String otp);
    void deleteAllByCreatedAtLessThan(Date date);
}
