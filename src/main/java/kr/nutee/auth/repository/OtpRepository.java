package kr.nutee.auth.repository;

import java.util.Date;
import kr.nutee.auth.domain.Otp;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OtpRepository extends MongoRepository<Otp,String> {
    Otp findOtpByOtpNumber(String otp);
    void deleteOtpByOtpNumber(String otp);
    void deleteAllByCreatedAtLessThan(Date date);
}
