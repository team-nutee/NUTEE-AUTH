package kr.nutee.auth.Service;

import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Enum.RoleType;
import kr.nutee.auth.Repository.MemberRepository;
import kr.nutee.auth.Repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OtpRepository otpRepository;

    public Member insertUser(Member member){
        member.setAccessedAt((LocalDateTime.now()));
        if(member.getSchoolEmail().equals("nutee.skhu.2020@gmail.com")){
            member.setRole(RoleType.MANAGER);
        }else{
            member.setRole(RoleType.USER);
        }
        return memberRepository.save(member);
    }
    public Member getUser(String userId){
        return memberRepository.findByUserId(userId);
    }

    public Boolean userIdCheck(String userId){
        return memberRepository.findByUserId(userId) == null;
    }

    public Boolean nicknameCheck(String nickname){
        return memberRepository.findByNickname(nickname) == null;
    }

    public Boolean emailCheck(String email){
        if(email.equals("nutee.skhu.2020@gmail.com")){//관리자계정
            return true;
        }else{
            return memberRepository.findBySchoolEmail(email) == null;
        }
    }

}
