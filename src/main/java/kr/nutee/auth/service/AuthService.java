package kr.nutee.auth.service;

import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Domain.Otp;
import kr.nutee.auth.Repository.MemberRepository;
import kr.nutee.auth.Repository.OtpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
@Slf4j
public class AuthService {
    @Autowired
    OtpRepository otpRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder bcryptEncoder;

    @Value("${mail.id}")
    private String mailId;

    @Value("${mail.password}")
    private String mailPassword;

    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.port}")
    private String mailPort;

    @Value("${mail.auth}")
    private String mailAuth;

    @Value("${mail.ssl.enable}")
    private String mailEnable;

    @Value("${mail.ssl.trust}")
    private String mailTrust;

    public Session getEmailSession(){
        Properties prop = new Properties();
        prop.put("mail.smtp.host", mailHost);
        prop.put("mail.smtp.port", mailPort);
        prop.put("mail.smtp.auth", mailAuth);
        prop.put("mail.smtp.ssl.enable", mailEnable);
        prop.put("mail.smtp.ssl.trust", mailTrust);

        return Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailId, mailPassword);
            }
        });
    }

    public Member findId(Member member){
        return memberRepository.findBySchoolEmail(member.getSchoolEmail());
    }

    public void findPassword(Member member){
        try {
            MimeMessage message = new MimeMessage(getEmailSession());
            message.setFrom(new InternetAddress(mailId));

            //수신자메일주소
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(member.getSchoolEmail()));

            //메일 제목 설정
            message.setSubject("[NUTEE] 새로 설정 된 비밀번호를 확인해 주세요."); //메일 제목을 입력

            //비밀번호 새로 설정
            member = memberRepository.findByUserId(member.getUserId());
            String newPw = generateNewPassword();
            member.setPassword(bcryptEncoder.encode(newPw));
            memberRepository.save(member);

            //메일 내용 입력
            message.setText(newPw);

            //메일 전송
            Transport.send(message);

            log.info("message sent successfully...");
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String generateOtpNumber(){
        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);
        int min = 10000;
        int max = 99999;
        return Integer.toString(rand.nextInt(max - min + 1)+ min);
    }

    public String generateNewPassword(){
        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);
        int min = 10000000;
        int max = 99999999;
        return Integer.toString(rand.nextInt(max - min + 1)+ min);
    }

    public void setOtp(String otpNumber){
        Otp otp = Otp.builder()
                .otpNumber(otpNumber)
                .build();
        otpRepository.save(otp);
    }

    public void sendOtp(String schoolEmail,String otp){
        try {
            MimeMessage message = new MimeMessage(getEmailSession());
            message.setFrom(new InternetAddress(mailId));

            //수신자메일주소
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(schoolEmail));

            // Subject
            message.setSubject("[NUTEE] 인증번호를 확인해주세요."); //메일 제목을 입력

            // Text
            message.setText(otp);    //메일 내용을 입력

            // send the message
            Transport.send(message); ////전송
            log.info("message sent successfully...");
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Boolean checkOtp(String otp){
        System.out.println("otp: "+otp);
        System.out.println(otpRepository.findByOtpNumber(otp));
        return otpRepository.findByOtpNumber(otp) != null;
    }

    //아이디 찾기
    //비밀번호 찾기

}
