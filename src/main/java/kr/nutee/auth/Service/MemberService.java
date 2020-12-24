package kr.nutee.auth.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Enum.ErrorCode;
import kr.nutee.auth.Enum.RoleType;
import kr.nutee.auth.Exception.NotAllowedException;
import kr.nutee.auth.Repository.MemberRepository;
import kr.nutee.auth.Repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OtpRepository otpRepository;

    @Autowired
    PasswordEncoder bcryptEncoder;

    @Value("${jwt.secret}")
    String secretKey;

    public Member insertUser(Member member){
        member.setAccessedAt((LocalDateTime.now()));
        if(member.getSchoolEmail().equals("nutee.skhu.2020@gmail.com")){
            member.setRole(RoleType.MANAGER);
        }else{
            member.setRole(RoleType.USER);
        }
        return memberRepository.save(member);
    }

    public Member getUser(HttpServletRequest request){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        String token = request.getHeader("Authorization").split(" ")[1];
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        Long memberId = body.get("id", Long.class);
        System.out.println(memberId);
        return memberRepository.findMemberById(memberId);
    }

    public void changePassword(Long memberId, Long requestId, String password) {
        if (!memberId.equals(requestId)) {
            throw new NotAllowedException("회원정보 불일치", ErrorCode.CONFLICT, HttpStatus.FORBIDDEN);
        }
        Member member = memberRepository.findMemberById(memberId);
        password = bcryptEncoder.encode(password);
        member.setPassword(password);
        memberRepository.save(member);
    }

    public Boolean checkUserId(String userId){
        return memberRepository.findMemberByUserId(userId) == null;
    }

    public Boolean checkNickname(String nickname){
        return memberRepository.findMemberByNickname(nickname) == null;
    }

    public Boolean checkEmail(String email){
        String adminEmail = "nutee.skhu.2020@gmail.com";
        if(email.equals(adminEmail)){
            return true;
        }
        return memberRepository.findMemberBySchoolEmail(email) == null;
    }

}
