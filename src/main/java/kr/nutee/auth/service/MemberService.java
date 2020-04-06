package kr.nutee.auth.service;

import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MemberService {
    @Autowired
    MemberRepository memberRepository;

    public Member getUser(String userId){
        return memberRepository.findByUserId(userId);
    }

    public Member insertUser(Member member){
        member.setCreatedAt(new Date());
        member.setUpdatedAt(new Date());
        member.setAccessedAt(new Date());
        member.setRole(0);
        return memberRepository.save(member);
    }
}
