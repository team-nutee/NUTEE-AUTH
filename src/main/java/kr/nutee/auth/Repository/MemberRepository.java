package kr.nutee.auth.Repository;

import kr.nutee.auth.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findMemberByUserId(String userId);
    Member findMemberById(Long memberId);
    Member findMemberByNickname(String nickname);
    Member findMemberBySchoolEmail(String schoolEmail);
}
