package kr.nutee.auth.Repository;

import kr.nutee.auth.Domain.Interest;
import kr.nutee.auth.Domain.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Long> {
    List<Major> findMajorsByMemberId(Long memberId);
}