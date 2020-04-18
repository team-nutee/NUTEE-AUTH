package kr.nutee.auth.Repository;

import kr.nutee.auth.Entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    void deleteAllByCreatedAtLessThan(Date date);
    Otp findByOtpNumber(String otp);
}
