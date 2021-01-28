package kr.nutee.auth.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.List;
import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Enum.ErrorCode;
import kr.nutee.auth.Enum.RoleType;
import kr.nutee.auth.Exception.NotAllowedException;
import kr.nutee.auth.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder bcryptEncoder;

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

    public Member getUserBy(HttpServletRequest request){
        String newSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        String token = request.getHeader("Authorization").split(" ")[1];
        Claims body = Jwts.parser().setSigningKey(newSecretKey).parseClaimsJws(token).getBody();
        Long memberId = body.get("id", Long.class);
        return memberRepository.findMemberById(memberId);
    }

    public String changeNickname(Member member, String nickname) {
        member.setNickname(nickname);
        Member updatedMember = memberRepository.save(member);
        return updatedMember.getNickname();
    }

    public List<String> changeInterests(Member member, List<String> interests) {
        member.setInterests(interests);
        Member updatedMember = memberRepository.save(member);
        return updatedMember.getInterests();
    }

    public List<String> changeMajors(Member member, List<String> majors) {
        member.setMajors(majors);
        Member updatedMember = memberRepository.save(member);
        return updatedMember.getInterests();
    }

    public String changeProfile(Member member, String profileUrl) {
        member.setProfileUrl(profileUrl);
        Member updatedMember = memberRepository.save(member);
        return updatedMember.getProfileUrl();
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
