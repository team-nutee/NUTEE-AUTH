package kr.nutee.auth.Repository;

import kr.nutee.auth.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUserId(String userId);
    Member findByNickname(String nickname);
    Member findBySchoolEmail(String schoolEmail);
}
