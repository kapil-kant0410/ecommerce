package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.*;
import com.ql.ecommerce.dto.auth.request.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthService {
    ResponseEntity<ApiResponse<Map<String, String>>> register(EmailPasswordRegisterRequest emailPasswordRegisterRequest);
    ResponseEntity<ApiResponse<Map<String, String>>> verifyEmail(Long userId,String token);
    ResponseEntity<ApiResponse<Map<String,String>>> resendEmailVerification(EmailRequest emailVerificationRequest);
    ResponseEntity<ApiResponse<Map<String, String>>> login(EmailPasswordLoginRequest emailPasswordLoginRequest);
    ResponseEntity<ApiResponse<Map<String,String>>> generateEmailOtp(EmailOtpLoginRequest emailOtpLoginRequest);
    ResponseEntity<ApiResponse<Map<String,String>>> validateEmailOtp(EmailOtpVerifyRequest emailOtpVerifyRequest);
    ResponseEntity<ApiResponse<Map<String,String>>> logout(RefreshTokenRequest refreshTokenRequest);
    ResponseEntity<ApiResponse<Map<String,String>>> refreshAccessToken(RefreshTokenRequest refreshTokenRequest);
    ResponseEntity<ApiResponse<Map<String,String>>> forgotPassword(EmailRequest emailRequest);
    ResponseEntity<ApiResponse<Map<String,String>>> resetPassword(Long userId ,String token,String newPassword);
    ResponseEntity<ApiResponse<Map<String,String>>> changePassword(ChangePasswordRequest changePasswordRequest);
}
