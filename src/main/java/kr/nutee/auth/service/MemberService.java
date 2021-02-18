package kr.nutee.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.List;
import kr.nutee.auth.domain.Member;
import kr.nutee.auth.dto.request.ChangePasswordRequest;
import kr.nutee.auth.dto.request.LoginDTO;
import kr.nutee.auth.enums.ErrorCode;
import kr.nutee.auth.enums.RoleType;
import kr.nutee.auth.exception.NotAllowedException;
import kr.nutee.auth.repository.MemberRepository;
import kr.nutee.auth.util.KafkaSenderTemplate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final AuthenticationManager authenticationManager;

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
        Member target = memberRepository.findMemberById(member.getId());
        target.setNickname(nickname);
        Member updatedMember = memberRepository.save(target);
        kafkaSenderTemplate.sendUpdateMember(member,target);
        return updatedMember.getNickname();
    }

    public List<String> changeInterests(Member member, List<String> interests) {
        Member target = memberRepository.findMemberById(member.getId());
        target.setInterests(interests);
        Member updatedMember = memberRepository.save(target);
        kafkaSenderTemplate.sendUpdateMember(member,target);
        return updatedMember.getInterests();
    }

    public List<String> changeMajors(Member member, List<String> majors) {
        Member target = memberRepository.findMemberById(member.getId());
        target.setMajors(majors);
        Member updatedMember = memberRepository.save(target);
        kafkaSenderTemplate.sendUpdateMember(member,target);
        return updatedMember.getMajors();
    }

    public String changeProfile(Member member, String profileUrl) {
        Member target = memberRepository.findMemberById(member.getId());
        target.setProfileUrl(profileUrl);
        Member updatedMember = memberRepository.save(target);
        kafkaSenderTemplate.sendUpdateMember(member,target);
        return updatedMember.getProfileUrl();
    }

    public void changePassword(Long memberId, ChangePasswordRequest request) {
        Member member = memberRepository.findMemberById(memberId);
        LoginDTO loginDTO = LoginDTO.builder()
            .userId(member.getUserId())
            .password(request.getNowPassword())
            .build();
        if (!authenticate(loginDTO)) {
            throw new NotAllowedException("현재 비밀번호가 일치하지 않습니다.",ErrorCode.ACCEPT_DENIED,HttpStatus.UNAUTHORIZED);
        }
        String changePassword = bcryptEncoder.encode(request.getChangePassword());
        member.setPassword(changePassword);
        memberRepository.save(member);
    }

    private boolean authenticate(LoginDTO request) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword()))
            .isAuthenticated();
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
