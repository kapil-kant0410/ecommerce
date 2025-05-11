package com.ql.ecommerce.repository;

import com.ql.ecommerce.entity.VerificationToken;
import com.ql.ecommerce.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByUserIdAndTokenType(Long userId, TokenType tokenType);
    void deleteByUserIdAndTokenType(Long userId,TokenType tokenType);
 }
