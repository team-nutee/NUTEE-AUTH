package kr.nutee.auth.repository;

import kr.nutee.auth.domain.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepository extends MongoRepository<Member,String> {
    Member findMemberBySchoolEmail(String schoolEmail);
    Member findMemberByUserId(String userId);
    Member findMemberById(Long memberId);
    Member findMemberByNickname(String nickname);
}
