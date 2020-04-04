package kr.nutee.auth.Repository;

import kr.nutee.auth.Domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUserId(String userId);
}
