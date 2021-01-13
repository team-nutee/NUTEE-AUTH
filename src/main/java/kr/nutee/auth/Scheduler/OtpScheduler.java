package kr.nutee.auth.Scheduler;

import kr.nutee.auth.Repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpScheduler {

    private final OtpRepository otpRepository;

    @Scheduled(cron = "0 * * * * *")//매 분 0초에 실행한다.
    @Transactional
    public void run() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE,-3);
        log.info(format.format(cal.getTime()));
        otpRepository.deleteAllByCreatedAtLessThan(cal.getTime());
    }
}
