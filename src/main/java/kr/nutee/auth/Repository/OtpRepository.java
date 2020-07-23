package kr.nutee.auth.Repository;

import kr.nutee.auth.Domain.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    void deleteAllByCreatedAtLessThan(Date date);
    void deleteOtpByOtpNumber(String otp);
    Otp findByOtpNumber(String otp);
}
