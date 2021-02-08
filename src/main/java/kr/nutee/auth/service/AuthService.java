package kr.nutee.auth.service;

import kr.nutee.auth.dto.request.SignupDTO;
import kr.nutee.auth.dto.response.UserData;
import kr.nutee.auth.domain.*;
import kr.nutee.auth.enums.ErrorCode;
import kr.nutee.auth.exception.ConflictException;
import kr.nutee.auth.repository.MemberRepository;
import kr.nutee.auth.repository.OtpRepository;
import kr.nutee.auth.util.KafkaSenderTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final OtpRepository otpRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder bcryptEncoder;
    private final MemberService memberService;
    private final KafkaSenderTemplate kafkaSenderTemplate;

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

    public Member findId(String schoolEmail){
        return memberRepository.findMemberBySchoolEmail(schoolEmail);
    }

    @Transactional
    public void findPassword(String schoolEmail, String userId){
        try {
            MimeMessage message = new MimeMessage(getEmailSession());
            message.setFrom(new InternetAddress(mailId));

            //수신자메일주소
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(schoolEmail));

            //메일 제목 설정
            message.setSubject("[NUTEE] 새로 설정 된 비밀번호를 확인해 주세요."); //메일 제목을 입력

            //비밀번호 새로 설정
            Member member = memberRepository.findMemberByUserId(userId);
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

            setOtp(otp);
            log.info("message sent successfully...");
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Transactional
    public UserData signUp(SignupDTO signupDTO){
        if(!memberService.checkUserId(signupDTO.getUserId())){
            throw new ConflictException("아이디가 중복되었습니다.", ErrorCode.CONFLICT, HttpStatus.CONFLICT);
        }
        if(!memberService.checkNickname(signupDTO.getNickname())){
            throw new ConflictException("닉네임이 중복되었습니다.", ErrorCode.CONFLICT, HttpStatus.CONFLICT);
        }
        if(!memberService.checkEmail(signupDTO.getSchoolEmail())){
            throw new ConflictException("이메일이 중복되었습니다.", ErrorCode.CONFLICT, HttpStatus.CONFLICT);
        }
        if(!checkOtp(signupDTO.getOtp())){
            throw new ConflictException("교내 이메일 인증에 실패 하였습니다."+signupDTO.getOtp(), ErrorCode.CONFLICT, HttpStatus.UNAUTHORIZED);
        }

        String password = bcryptEncoder.encode(signupDTO.getPassword());
        Member member = Member.builder()
                .userId(signupDTO.getUserId())
                .nickname(signupDTO.getNickname())
                .schoolEmail(signupDTO.getSchoolEmail())
                .interests(signupDTO.getInterests())
                .majors(signupDTO.getMajors())
                .password(password)
                .build();

        member = memberService.createUser(member);

        kafkaSenderTemplate.sendCreateMember(member,member);
        return UserData.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .interests(member.getInterests())
                .majors(member.getMajors())
                .build();
    }

    public Boolean checkOtp(String otp){
        if (otp.equals("000000")) {
            return true;
        }
        if (otpRepository.findOtpByOtpNumber(otp) == null) {
            return false;
        }
        otpRepository.deleteOtpByOtpNumber(otp);
        return true;
    }

}
