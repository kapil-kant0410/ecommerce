package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.*;
import com.ql.ecommerce.dto.auth.request.*;
import com.ql.ecommerce.service.AuthService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String,String>>> registerUser(@Valid @RequestBody EmailPasswordRegisterRequest emailPasswordRegisterRequest){
        return authService.register(emailPasswordRegisterRequest);
    }

    @PostMapping("/verify-email")
    ResponseEntity<ApiResponse<Map<String,Object>>> verifyEmail(@RequestParam Long userId,@RequestParam String token){
        return authService.verifyEmail(userId,token);
    }

    @PostMapping("/resend-email-verification")
    public ResponseEntity<ApiResponse<Map<String,Object>>> resendEmailVerification(@Valid @RequestBody EmailRequest emailVerificationRequest){
        return authService.resendEmailVerification(emailVerificationRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,Object>>> loginByPassword(@Valid @RequestBody EmailPasswordLoginRequest emailPasswordLoginRequest) {
        return authService.login(emailPasswordLoginRequest);
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<ApiResponse<Map<String,Object>>> generateEmailOtp(@Valid @RequestBody EmailOtpLoginRequest emailOtpLoginRequest) {
        return authService.generateEmailOtp(emailOtpLoginRequest);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<ApiResponse<Map<String,Object>>> validateEmailOtp(@Valid @RequestBody EmailOtpVerifyRequest emailOtpVerifyRequest) {
        return authService.validateEmailOtp(emailOtpVerifyRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String,Object>>> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.logout(refreshTokenRequest);
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<ApiResponse<Map<String,Object>>> refreshAccessToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshAccessToken(refreshTokenRequest);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String,Object>>> forgotPassword(@Valid @RequestBody EmailRequest emailRequest) {
        return authService.forgotPassword(emailRequest);
    }

    @Transactional
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Map<String,Object>>> resetPassword(@RequestParam Long userId,@RequestParam String token,@RequestParam String newPassword) {
        return authService.resetPassword(userId,token,newPassword);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Map<String,Object>>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest){
         return authService.changePassword(changePasswordRequest);
    }

}
