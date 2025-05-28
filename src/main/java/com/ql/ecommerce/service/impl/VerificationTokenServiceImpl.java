package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.entity.VerificationToken;
import com.ql.ecommerce.enums.TokenType;
import com.ql.ecommerce.repository.VerificationTokenRepository;
import com.ql.ecommerce.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Value("${app.token.verification.expiry-minutes:30}")
    private int verificationTokenExpiry;

    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public VerificationTokenServiceImpl(VerificationTokenRepository tokenRepository, PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String createAndSaveVerificationToken(User user, TokenType tokenType) {
        String token = UUID.randomUUID().toString();  // Raw token
        String tokenHash = passwordEncoder.encode(token); // Hashed token

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setTokenHash(tokenHash);
        verificationToken.setTokenType(tokenType);
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(verificationTokenExpiry));
        verificationToken.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(verificationToken);

        return token;
    }
}

