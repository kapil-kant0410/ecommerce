package com.ql.ecommerce.repository;

import com.ql.ecommerce.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefTokenAndUserId(String token,Long userId);
    void deleteByRefTokenAndUserId(String token,Long userId);
}

