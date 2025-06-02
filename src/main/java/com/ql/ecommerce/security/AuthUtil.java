package com.ql.ecommerce.security;

import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.exception.Unauthorized;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUser();
        }

        throw new Unauthorized("User not authenticated");
    }

    public String getCurrentUserEmail() {
      User user=getCurrentUser();
      return user.getEmail();
    }

    public Long getCurrentUserId(){
        User user=getCurrentUser();
        return user.getId();
    }

}
