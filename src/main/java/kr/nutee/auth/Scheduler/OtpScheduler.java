package kr.nutee.auth.Scheduler;

import kr.nutee.auth.Repository.OtpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OtpScheduler {
    @Autowired
    OtpRepository otpRepository;

//    @Scheduled(cron = "0 * * * * *")//매 분 0초에 실행한다.
//    @Transactional
//    public void run() {
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.MINUTE,-3);
//        System.out.println(format.format(cal.getTime()));
//        otpRepository.deleteAllByCreatedAtLessThan(cal.getTime());
//    }
}