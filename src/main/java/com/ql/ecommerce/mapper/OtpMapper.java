package com.ql.ecommerce.mapper;

import com.ql.ecommerce.entity.Otp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OtpMapper {
    public Otp toEntity(String email,String randomOtp){
        Otp otp=new Otp();
        otp.setEmail(email);
        otp.setOtp(randomOtp);
        otp.setGeneratedAt(LocalDateTime.now());
        return otp;
    }
}
