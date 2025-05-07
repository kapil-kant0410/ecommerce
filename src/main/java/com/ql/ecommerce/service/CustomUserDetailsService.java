package com.ql.ecommerce.service;

import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.exception.UserNotFoundException;
import com.ql.ecommerce.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.ql.ecommerce.enums.Role;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email){
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User not found with email: \" + email"));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRole())
        );
    }

    public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role){
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

}
