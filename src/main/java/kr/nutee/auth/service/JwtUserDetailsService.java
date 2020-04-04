package kr.nutee.auth.service;

import kr.nutee.auth.Domain.User;
import kr.nutee.auth.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = repository.findByUserId(userId);

        List<GrantedAuthority> roles = new ArrayList<>();

        if (user == null) {
            throw new UsernameNotFoundException("User not found with userId: " + userId);
        }
        if (user.getRole() == 0) {
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return new org.springframework.security.core.userdetails.User(user.getNickname(), user.getPassword(), roles);
    }

}
