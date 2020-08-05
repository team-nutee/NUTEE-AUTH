package kr.nutee.auth.Repository;

import kr.nutee.auth.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUserId(String userId);
    Member findMemberById(Long memberId);
    Member findByNickname(String nickname);
    Member findBySchoolEmail(String schoolEmail);
}
