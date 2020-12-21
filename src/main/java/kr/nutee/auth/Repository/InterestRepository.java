package kr.nutee.auth.Repository;

import kr.nutee.auth.Domain.Interest;
import kr.nutee.auth.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findInterestsByMemberId(Long memberId);
}
