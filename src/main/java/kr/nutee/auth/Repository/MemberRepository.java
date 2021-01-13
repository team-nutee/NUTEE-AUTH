package kr.nutee.auth.Repository;

import kr.nutee.auth.Domain.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepository extends MongoRepository<Member,String> {
    Member findMemberBySchoolEmail(String schoolEmail);
    Member findMemberByUserId(String userId);
    Member findMemberById(Long memberId);
    Member findMemberByNickname(String nickname);
}
