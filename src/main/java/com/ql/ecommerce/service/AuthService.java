package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.*;
import com.ql.ecommerce.dto.auth.request.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthService {
    ResponseEntity<ApiResponse<Map<String,String>>>  register(EmailPasswordRegisterRequest emailPasswordRegisterRequest);
    ResponseEntity<ApiResponse<Map<String,Object>>>  verifyEmail(Long userId,String token);
    ResponseEntity<ApiResponse<Map<String,Object>>>  resendEmailVerification(EmailRequest emailVerificationRequest);
    ResponseEntity<ApiResponse<Map<String,Object>>>  login(EmailPasswordLoginRequest emailPasswordLoginRequest);
    ResponseEntity<ApiResponse<Map<String,Object>>>  generateEmailOtp(EmailOtpLoginRequest emailOtpLoginRequest);
    ResponseEntity<ApiResponse<Map<String,Object>>>  validateEmailOtp(EmailOtpVerifyRequest emailOtpVerifyRequest);
    ResponseEntity<ApiResponse<Map<String,Object>>>  logout(RefreshTokenRequest refreshTokenRequest);
    ResponseEntity<ApiResponse<Map<String,Object>>>  refreshAccessToken(RefreshTokenRequest refreshTokenRequest);
    ResponseEntity<ApiResponse<Map<String,Object>>>  forgotPassword(EmailRequest emailRequest);
    ResponseEntity<ApiResponse<Map<String,Object>>>  resetPassword(Long userId ,String token,String newPassword);
    ResponseEntity<ApiResponse<Map<String,Object>>>  changePassword(ChangePasswordRequest changePasswordRequest);
}
