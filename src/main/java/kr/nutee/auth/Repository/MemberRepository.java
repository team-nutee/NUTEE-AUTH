package kr.nutee.auth.Repository;

import kr.nutee.auth.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    Member findByUserId(String userId);
}
