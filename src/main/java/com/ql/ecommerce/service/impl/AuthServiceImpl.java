package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.auth.request.*;
import com.ql.ecommerce.entity.Otp;
import com.ql.ecommerce.entity.RefreshToken;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.entity.VerificationToken;
import com.ql.ecommerce.enums.TokenType;
import com.ql.ecommerce.exception.*;
import com.ql.ecommerce.mapper.OtpMapper;
import com.ql.ecommerce.mapper.RefreshTokenMapper;
import com.ql.ecommerce.mapper.UserMapper;
import com.ql.ecommerce.repository.OtpRepository;
import com.ql.ecommerce.repository.RefreshTokenRepository;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.repository.VerificationTokenRepository;
import com.ql.ecommerce.security.JwtUtil;
import com.ql.ecommerce.service.AuthService;
import com.ql.ecommerce.service.CustomUserDetailsService;
import com.ql.ecommerce.service.MailService;
import com.ql.ecommerce.service.VerificationTokenService;
import com.ql.ecommerce.util.ResponseBuilder;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random=new Random();
    private final OtpRepository otpRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ResponseBuilder responseBuilder;
    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;
    private final OtpMapper otpMapper;
    Logger logger= LoggerFactory.getLogger(AuthServiceImpl.class);

    @Value("${otp.subject}")
    private String otpSubject;

    @Value("${otp.message}")
    private String otpMessage;

    @Value("${jwt.access_token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh_token.expiration}")
    private Long refreshTokenExpiration;

    @Value("${app.verification.token.expiry-minutes}")
    private Long verificationTokenExpiry;

    public AuthServiceImpl(OtpMapper otpMapper,RefreshTokenMapper refreshTokenMapper,MailService mailService,VerificationTokenService verificationTokenService,UserMapper userMapper,ResponseBuilder responseBuilder,RefreshTokenRepository refreshTokenRepository,VerificationTokenRepository verificationTokenRepository,CustomUserDetailsService customUserDetailsService,OtpRepository otpRepository,PasswordEncoder passwordEncoder,UserRepository userRepository,AuthenticationManager authenticationManager,JwtUtil jwtUtil){
        this.authenticationManager=authenticationManager;
        this.jwtUtil=jwtUtil;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.otpRepository=otpRepository;
        this.customUserDetailsService=customUserDetailsService;
        this.verificationTokenRepository=verificationTokenRepository;
        this.refreshTokenRepository=refreshTokenRepository;
        this.responseBuilder=responseBuilder;
        this.userMapper=userMapper;
        this.verificationTokenService=verificationTokenService;
        this.mailService=mailService;
        this.refreshTokenMapper=refreshTokenMapper;
        this.otpMapper=otpMapper;
    }

    //registering user and sending verification link to the registered user
    public ResponseEntity<ApiResponse<Map<String,String>>> register(EmailPasswordRegisterRequest emailPasswordRegisterRequest){

        if(userRepository.existsByEmail(emailPasswordRegisterRequest.getEmail())){
            throw new IllegalArgumentException("Email already exists.");
        }

        User user=userMapper.toEntity(emailPasswordRegisterRequest);
        userRepository.save(user);

        String token=verificationTokenService.createAndSaveVerificationToken(user,TokenType.EMAIL_VERIFICATION);

        String verificationLink = "http://localhost:8080/api/auth/verify-email?userId="+user.getId()+"&&token="+ token;

        mailService.sendEmail(user.getEmail(),"Email verification","Click the following link to verify:\n" + verificationLink);

        ApiResponse<Map<String,String>> apiResponse=ApiResponse.success(HttpStatus.CREATED.value(), Collections.emptyMap(),"Registered please verify your mail");
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

    //verifying email by token that is received while registering user and delete verification token from table
    public ResponseEntity<ApiResponse<Map<String,Object>>> verifyEmail(Long userId,String token){

       VerificationToken verificationToken=verificationTokenRepository.findByUserIdAndTokenType(userId,TokenType.EMAIL_VERIFICATION).orElseThrow(()-> new VerificationTokenNotFound("Email verification token not found"));

       if(!passwordEncoder.matches(token,verificationToken.getTokenHash())){
        throw new IllegalArgumentException("Invalid token");
       }

       if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now()) || verificationToken.isUsed()) {
        throw new IllegalArgumentException("Token is expired");
       }

       User user = verificationToken.getUser();
       user.setEmailVerified(true);
       userRepository.save(user);

       verificationTokenRepository.delete(verificationToken);

       return responseBuilder.build(Collections.emptyMap(),"Verification successful");
    }

    //generating email verification token again if user does missed verification token while registering deleting old email verification token
    @Transactional
    public ResponseEntity<ApiResponse<Map<String,Object>>> resendEmailVerification(EmailRequest emailRequest){
          String email=emailRequest.getEmail();
          User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFound("User not found with this email"));

          if(user.isEmailVerified()){
              throw new IllegalArgumentException("Email is already verified");
          }

          verificationTokenRepository.deleteByUserIdAndTokenType(user.getId(),TokenType.EMAIL_VERIFICATION);
          String token=verificationTokenService.createAndSaveVerificationToken(user,TokenType.EMAIL_VERIFICATION);

          String verificationLink = "http://localhost:8080/api/auth/verify-email?userId="
                + user.getId() +  "&&token=" + token;

          mailService.sendEmail(user.getEmail(),"Resend Email Verification","Click the following link to verify your email:\n" + verificationLink);

          return responseBuilder.build(Collections.emptyMap(),"Verification email resent successfully");
    }

    //login by email and password
    public ResponseEntity<ApiResponse<Map<String,Object>>> login(EmailPasswordLoginRequest emailPasswordLoginRequest){

        User user=userRepository.findByEmail(emailPasswordLoginRequest.getEmail()).orElseThrow(()->new UserNotFound("User not found with this email."));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        emailPasswordLoginRequest.getEmail(),
                        emailPasswordLoginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //generating jwt tokens
        String accessToken = jwtUtil.generateJwtToken(userDetails,user.getId(),accessTokenExpiration);
        String refreshToken=jwtUtil.generateJwtToken(userDetails,user.getId(),refreshTokenExpiration);

        Map<String,String> data=new HashMap<>();
        data.put("access_token",accessToken);
        data.put("refresh_token",refreshToken);

        RefreshToken refreshTokenEntity=refreshTokenMapper.toEntity(user,refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return responseBuilder.build("Tokens",data,"User Logged In");
    }

    //generating email otp
    public ResponseEntity<ApiResponse<Map<String,Object>>> generateEmailOtp(EmailOtpLoginRequest emailOtpLoginRequest){

        userRepository.findByEmail(emailOtpLoginRequest.getEmail()).orElseThrow(()->new UserNotFound("User not found with this email."));
        String randomOtp=String.valueOf(random.nextInt(900000)+100000);

        Otp otp=otpMapper.toEntity(emailOtpLoginRequest.getEmail(),randomOtp);
        otpRepository.save(otp);

        mailService.sendEmail(emailOtpLoginRequest.getEmail(),otpSubject,String.format(otpMessage,otp.getOtp()));

        return responseBuilder.build(Collections.emptyMap(),"Otp send successfully");

    }

    //validating otp from otps table
    public ResponseEntity<ApiResponse<Map<String,Object>>> validateEmailOtp(EmailOtpVerifyRequest emailOtpVerifyRequest){

        User user=userRepository.findByEmail(emailOtpVerifyRequest.getEmail()).orElseThrow(()->new UserNotFound("User Not found with this email."));
        Otp otp=otpRepository.findTopByEmailOrderByGeneratedAtDesc(emailOtpVerifyRequest.getEmail()).orElseThrow(()-> new OtpNotFound("Otp not found for this email"));

        if (otp.getGeneratedAt().isBefore(LocalDateTime.now().minusMinutes(20))) {
            throw new IllegalArgumentException("Otp expired");
        }

        if (!otp.getOtp().equals(emailOtpVerifyRequest.getOtp())) {
            throw new IllegalArgumentException("Invalid otp");
        }

        otpRepository.delete(otp);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(emailOtpVerifyRequest.getEmail());
        String accessToken=jwtUtil.generateJwtToken(userDetails,user.getId(),accessTokenExpiration);
        String refreshToken=jwtUtil.generateJwtToken(userDetails,user.getId(),refreshTokenExpiration);

        RefreshToken refreshTokenEntity=refreshTokenMapper.toEntity(user,refreshToken);

        refreshTokenRepository.save(refreshTokenEntity);

        Map<String,String> data=new HashMap<>();
        data.put("access_token",accessToken);
        data.put("refresh_token",refreshToken);

        return responseBuilder.build("tokens",data,"Login Successfully");
    }

    //deleting refresh token from refresh_tokens table
    @Transactional
    public ResponseEntity<ApiResponse<Map<String,Object>>> logout(RefreshTokenRequest refreshTokenRequest){

        if(!jwtUtil.validateJwtToken(refreshTokenRequest.getRefreshToken())){
            throw new InvalidToken("token is invalid");
        }

        Long userId= jwtUtil.getUserIdFromToken(refreshTokenRequest.getRefreshToken());

       refreshTokenRepository.findByRefTokenAndUserId(refreshTokenRequest.getRefreshToken(),userId).orElseThrow(()->new RefreshTokenNotFound("Refresh token not found"));
       refreshTokenRepository.deleteByRefTokenAndUserId(refreshTokenRequest.getRefreshToken(),userId);

      return responseBuilder.build(Collections.emptyMap(),"Logged out successfully");
    }

    //generating access token and refresh token on valid refresh token and deleting old refresh token
    public ResponseEntity<ApiResponse<Map<String,Object>>> refreshAccessToken(RefreshTokenRequest refreshTokenRequest){

          if(!jwtUtil.validateJwtToken(refreshTokenRequest.getRefreshToken())){
              throw new InvalidToken("token is invalid");
          }

          Long userId= jwtUtil.getUserIdFromToken(refreshTokenRequest.getRefreshToken());
          String email=jwtUtil.getUserNameFromJwtToken(refreshTokenRequest.getRefreshToken());

          RefreshToken oldRefreshToken=refreshTokenRepository.findByRefTokenAndUserId(refreshTokenRequest.getRefreshToken(),userId).orElseThrow(()->new RefreshTokenNotFound("Invalid refresh token already logged out"));

          if (oldRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(oldRefreshToken);
            throw new TokenExpired("Refresh token expired");
          }

          UserDetails userDetails=customUserDetailsService.loadUserByUsername(email);
          String accessToken=jwtUtil.generateJwtToken(userDetails,userId,accessTokenExpiration);
          String refreshToken=jwtUtil.generateJwtToken(userDetails,userId,refreshTokenExpiration);

          refreshTokenMapper.updateRefreshToken(oldRefreshToken,refreshToken);

          Map<String,String> data=new HashMap<>();
          data.put("access_token",accessToken);
          data.put("refresh_token",refreshToken);

          return responseBuilder.build("Tokens",data,"New access and refresh tokens");

    }

    //generating password reset token for that user
    public ResponseEntity<ApiResponse<Map<String,Object>>> forgotPassword(EmailRequest emailRequest){

        User user = userRepository.findByEmail(emailRequest.getEmail()).orElseThrow(()->new UserNotFound("User not found with this email"));

        String token=verificationTokenService.createAndSaveVerificationToken(user,TokenType.FORGOT_PASSWORD);

        String resetLink = "http://localhost:8080/api/auth/reset-password?userId=" + user.getId() +
                "&&token=" + token;

        mailService.sendEmail(user.getEmail(),"Reset your password","Click the following link to reset your password:\n" + resetLink);

        return responseBuilder.build(Collections.emptyMap(),"Password reset link sent");
    }

    //verifying reset password token delete it from verification_token table and update new password
    public ResponseEntity<ApiResponse<Map<String,Object>>> resetPassword(Long userId ,String token,String newPassword){

        VerificationToken verificationToken = verificationTokenRepository.findByUserIdAndTokenType(userId,TokenType.FORGOT_PASSWORD).orElseThrow(()->new VerificationTokenNotFound("Password reset token not found"));

        if(!passwordEncoder.matches(token,verificationToken.getTokenHash())){
            throw new IllegalArgumentException("Invalid token");
        }

        if(verificationToken.getExpiresAt().isBefore(LocalDateTime.now())||verificationToken.isUsed()){
            throw new IllegalArgumentException("Token expired or already used");
        }

        userMapper.updatePassword(verificationToken.getUser(),newPassword);
        verificationTokenRepository.deleteByUserIdAndTokenType(userId,TokenType.FORGOT_PASSWORD);

        return responseBuilder.build(Collections.emptyMap(),"Password reset successful");
    }

    //change password with the new password
    public ResponseEntity<ApiResponse<Map<String,Object>>> changePassword(ChangePasswordRequest changePasswordRequest){
        if (Objects.equals(changePasswordRequest.getNewPassword(), changePasswordRequest.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }

        User user=userRepository.findById(changePasswordRequest.getUserId()).orElseThrow(()-> new UserNotFound("User not found with this userId"));

        if(!passwordEncoder.matches(changePasswordRequest.getPassword(),user.getPassword())){
          throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        return responseBuilder.build(Collections.emptyMap(),"Password changed successfully");
    }

}
