package kr.nutee.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.List;
import kr.nutee.auth.domain.Member;
import kr.nutee.auth.enums.ErrorCode;
import kr.nutee.auth.enums.RoleType;
import kr.nutee.auth.exception.NotAllowedException;
import kr.nutee.auth.repository.MemberRepository;
import kr.nutee.auth.util.KafkaSenderTemplate;
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

    private final KafkaSenderTemplate kafkaSenderTemplate;




    @Value("${jwt.secret}")
    String secretKey;

    public Member createUser(Member member) {
        LocalDateTime now = LocalDateTime.now();
        member.setCreatedAt(now);
        member.setUpdatedAt(now);
        member.setAccessedAt(now);
        if(member.getSchoolEmail().equals("nutee.skhu.2020@gmail.com")){
            member.setRole(RoleType.MANAGER);
        }else{
            member.setRole(RoleType.USER);
        }
        return memberRepository.save(member);
    }

    public void deleteUser(Member member) {
        Member target = memberRepository.findMemberById(member.getId());
        memberRepository.delete(target);
    }

    public void updateUser(Member member) {
        Member target = memberRepository.findMemberById(member.getId());
        target = memberRepository.save(target);
        kafkaSenderTemplate.sendUpdateMember(member,target);
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
        return updatedMember.getMajors();
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
