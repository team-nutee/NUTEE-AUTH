package kr.nutee.auth.Repository;

import kr.nutee.auth.Domain.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Integer> {
    Member findByNickname(String username);
    Member findBySchoolEmail(String email);
    Long deleteByNickname(String username);
}
