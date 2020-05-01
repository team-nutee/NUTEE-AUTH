package kr.nutee.auth.service;

import kr.nutee.auth.Entity.Member;
import kr.nutee.auth.Entity.RoleType;
import kr.nutee.auth.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    public Member getUser(String userId){
        Member member = memberRepository.findByUserId(userId);
        System.out.println(member);
        return member;
    }

    public Member insertUser(Member member){
        member.setAccessedAt(new Date());
        if(member.getSchoolEmail().equals("nutee.skhu.2020@gmail.com")){
            member.setRole(RoleType.MANAGER);
        }else{
            member.setRole(RoleType.USER);
        }
        return memberRepository.save(member);
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
