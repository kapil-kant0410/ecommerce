package com.ql.ecommerce.service;

import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.enums.TokenType;
import org.springframework.stereotype.Service;

@Service
public interface VerificationTokenService {
    String createAndSaveVerificationToken(User user, TokenType tokenType);
}
