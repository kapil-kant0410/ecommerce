package com.ql.ecommerce.repository;

import com.ql.ecommerce.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findTopByEmailOrderByGeneratedAtDesc(String email);
    void deleteByGeneratedAtBefore(LocalDateTime time);
}
