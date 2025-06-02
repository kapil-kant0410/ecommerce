package com.ql.ecommerce.service;

import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.exception.UserNotFound;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    Logger logger= LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email){
        logger.info("In customUserDetailsService loading user from db and send back to auth filter");
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with email: \" + email"));
        return new CustomUserDetails(user);
    }

}
