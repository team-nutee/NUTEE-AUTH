package kr.nutee.auth.service;

import kr.nutee.auth.Domain.User;
import kr.nutee.auth.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User getUser(String userId){
        return userRepository.findByUserId(userId);
    }

    public User insertUser(User user){
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setAccessedAt(new Date());
        user.setRole(0);
        return userRepository.insert(user);
    }
}
