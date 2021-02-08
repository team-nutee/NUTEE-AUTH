package kr.nutee.auth.service;

import kr.nutee.auth.domain.Member;
import kr.nutee.auth.enums.RoleType;
import kr.nutee.auth.repository.MemberRepository;
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
    private MemberRepository repository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = repository.findMemberByUserId(userId);
        List<GrantedAuthority> roles = new ArrayList<>();
        if (member == null) {
            throw new UsernameNotFoundException("User not found with userId: " + userId);
        }

        if (member.getRole() == RoleType.USER) {
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else if(member.getRole() == RoleType.MANAGER) {
            roles.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
        } else if(member.getRole() == RoleType.ADMIN) {
            roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if(member.getRole() == RoleType.GUEST) {
            roles.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        }
        return new org.springframework.security.core.userdetails.User(member.getUserId(), member.getPassword(), roles);
    }

}
