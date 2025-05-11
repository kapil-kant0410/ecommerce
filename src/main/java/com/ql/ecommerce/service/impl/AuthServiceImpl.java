package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.auth.request.*;
import com.ql.ecommerce.entity.Otp;
import com.ql.ecommerce.entity.RefreshToken;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.entity.VerificationToken;
import com.ql.ecommerce.enums.Role;
import com.ql.ecommerce.enums.TokenType;
import com.ql.ecommerce.exception.*;
import com.ql.ecommerce.repository.OtpRepository;
import com.ql.ecommerce.repository.RefreshTokenRepository;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.repository.VerificationTokenRepository;
import com.ql.ecommerce.security.JwtUtil;
import com.ql.ecommerce.service.AuthService;
import com.ql.ecommerce.service.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

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
    private final JavaMailSender javaMailSender;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
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

    public AuthServiceImpl(RefreshTokenRepository refreshTokenRepository,VerificationTokenRepository verificationTokenRepository,CustomUserDetailsService customUserDetailsService,JavaMailSender javaMailSender,OtpRepository otpRepository,PasswordEncoder passwordEncoder,UserRepository userRepository,AuthenticationManager authenticationManager,JwtUtil jwtUtil){
        this.authenticationManager=authenticationManager;
        this.jwtUtil=jwtUtil;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.otpRepository=otpRepository;
        this.javaMailSender=javaMailSender;
        this.customUserDetailsService=customUserDetailsService;
        this.verificationTokenRepository=verificationTokenRepository;
        this.refreshTokenRepository=refreshTokenRepository;
    }

    //registering user and sending verification link to the registered user
    public ResponseEntity<ApiResponse<Map<String,String>>> register(EmailPasswordRegisterRequest registerRequestDto){

        if(userRepository.existsByEmail(registerRequestDto.getEmail())){
            ApiResponse<Map<String,String>> apiResponse=  ApiResponse.error(HttpStatus.CONFLICT.value(), null,"Email already exists.");
            return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setName(registerRequestDto.getName());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setRole(Role.valueOf(registerRequestDto.getRole()));
        userRepository.save(user);

        //verify the user email
        String token = UUID.randomUUID().toString(); // generate a unique token
        String tokenHash = passwordEncoder.encode(token); // hash the token
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setTokenHash(tokenHash);
        verificationToken.setTokenType(TokenType.EMAIL_VERIFICATION);
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(verificationTokenExpiry)); // 30 minutes expiration
        verificationToken.setCreatedAt(LocalDateTime.now());

        // Save token to the database
        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/api/auth/verify-email?userId="+user.getId()+"&&token"+ token;

        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("Email verification");
        simpleMailMessage.setText("Click the following link to verify:\n" + verificationLink);
        javaMailSender.send(simpleMailMessage);

        ApiResponse<Map<String,String>> apiResponse=ApiResponse.success(HttpStatus.CREATED.value(), Collections.emptyMap(),"Registered please verify your mail");
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);

    }

    //verifying email by token that is received while registering user and delete verification token from table
    public ResponseEntity<ApiResponse<Map<String,String>>> verifyEmail(Long userId,String token){

    VerificationToken verificationToken=verificationTokenRepository.findByUserIdAndTokenType(userId,TokenType.EMAIL_VERIFICATION).orElseThrow(()-> new VerificationTokenNotFoundException("Email verification token not found"));

    if(!passwordEncoder.matches(token,verificationToken.getTokenHash())){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), Collections.emptyMap(), "Invalid token"));
    }

    if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now()) || verificationToken.isUsed()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), Collections.emptyMap(), "Token is expired or already used."));
    }

    User user = verificationToken.getUser();
    user.setEmailVerified(true);
    userRepository.save(user);

    verificationTokenRepository.delete(verificationToken);

    return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), Collections.emptyMap(), "Verification successful"));
    }

    //generating email verification token again if user does missed verification token while registering deleting old email verification token
    @Transactional
    public ResponseEntity<ApiResponse<Map<String,String>>> resendEmailVerification(EmailRequest emailRequest){
          String email=emailRequest.getEmail();
          User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found with this email"));

          if(user.isEmailVerified()){
              ApiResponse<Map<String, String>> response = ApiResponse.error(
                      HttpStatus.BAD_REQUEST.value(), null, "Email is already verified");
              return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
          }

          verificationTokenRepository.deleteByUserIdAndTokenType(user.getId(),TokenType.EMAIL_VERIFICATION);

          String token = UUID.randomUUID().toString(); // Plain token
          String tokenHash = passwordEncoder.encode(token); // Hashed token

          VerificationToken verificationToken = new VerificationToken();
          verificationToken.setUser(user);
          verificationToken.setTokenHash(tokenHash);
          verificationToken.setTokenType(TokenType.EMAIL_VERIFICATION);
          verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(verificationTokenExpiry));
          verificationToken.setCreatedAt(LocalDateTime.now());

          verificationTokenRepository.save(verificationToken);

          String verificationLink = "http://localhost:8080/api/auth/verify-email?userId="
                + user.getId() +  "&&token=" + token;

         SimpleMailMessage mail = new SimpleMailMessage();
         mail.setTo(user.getEmail());
         mail.setSubject("Resend Email Verification");
         mail.setText("Click the following link to verify your email:\n" + verificationLink);

         javaMailSender.send(mail);

        ApiResponse<Map<String, String>> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(), Collections.emptyMap(), "Verification email resent successfully");

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    //login by email and password
    public ResponseEntity<ApiResponse<Map<String,String>>> login(EmailPasswordLoginRequest emailPasswordLoginRequest){

        User user=userRepository.findByEmail(emailPasswordLoginRequest.getEmail()).orElseThrow(()->new UserNotFoundException("User not found with this email."));

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

        RefreshToken refreshTokenEntity=new RefreshToken();
        refreshTokenEntity.setRefToken(refreshToken);
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenEntity.setUser(user);
        refreshTokenRepository.save(refreshTokenEntity);


        ApiResponse<Map<String,String>> apiResponse=ApiResponse.success(HttpStatus.CREATED.value(), data,"User Logged In");
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    //generating email otp
    public ResponseEntity<ApiResponse<Map<String,String>>> generateEmailOtp(EmailOtpLoginRequest emailOtpLoginRequest){

        userRepository.findByEmail(emailOtpLoginRequest.getEmail()).orElseThrow(()->new UserNotFoundException("User not found with this email."));
        String randomOtp=String.valueOf(random.nextInt(900000)+100000);

        Otp otp=new Otp();
        otp.setEmail(emailOtpLoginRequest.getEmail());
        otp.setOtp(randomOtp);
        otp.setGeneratedAt(LocalDateTime.now());

        otpRepository.save(otp);

        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setTo(emailOtpLoginRequest.getEmail());
        simpleMailMessage.setSubject(otpSubject);
        simpleMailMessage.setText(String.format(otpMessage,otp.getOtp()));

        javaMailSender.send(simpleMailMessage);

        ApiResponse<Map<String,String>> apiResponse=ApiResponse.success(HttpStatus.OK.value(), Collections.emptyMap(),"Otp send successfully");
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);

    }

    //validating otp from otps table
    public ResponseEntity<ApiResponse<Map<String,String>>> validateEmailOtp(EmailOtpVerifyRequest emailOtpVerifyRequest){

        User user=userRepository.findByEmail(emailOtpVerifyRequest.getEmail()).orElseThrow(()->new UserNotFoundException("User Not found with this email."));
        Otp otp=otpRepository.findTopByEmailOrderByGeneratedAtDesc(emailOtpVerifyRequest.getEmail()).orElseThrow(()-> new OtpNotFoundException("Otp not found for this email"));

        if (otp.getGeneratedAt().isBefore(LocalDateTime.now().minusMinutes(20))) {
            ApiResponse<Map<String,String>> apiResponse=ApiResponse.error(HttpStatus.BAD_REQUEST.value(), null,"Otp expired");
            return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
        }

        if (!otp.getOtp().equals(emailOtpVerifyRequest.getOtp())) {
            ApiResponse<Map<String,String>> apiResponse=ApiResponse.error(HttpStatus.BAD_REQUEST.value(), null,"Invalid otp");
            return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
        }

        otpRepository.delete(otp);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(emailOtpVerifyRequest.getEmail());
        String accessToken=jwtUtil.generateJwtToken(userDetails,user.getId(),accessTokenExpiration);
        String refreshToken=jwtUtil.generateJwtToken(userDetails,user.getId(),refreshTokenExpiration);

        RefreshToken refreshTokenEntity=new RefreshToken();
        refreshTokenEntity.setRefToken(refreshToken);
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenEntity.setUser(user);
        refreshTokenRepository.save(refreshTokenEntity);

        Map<String,String> data=new HashMap<>();
        data.put("access_token",accessToken);
        data.put("refresh_token",refreshToken);

        ApiResponse<Map<String,String>> apiResponse=ApiResponse.success(HttpStatus.OK.value(), data,"Login successfully");
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    //deleting refresh token from refresh_tokens table
    @Transactional
    public ResponseEntity<ApiResponse<Map<String,String>>> logout(RefreshTokenRequest refreshTokenRequest){

        if(!jwtUtil.validateJwtToken(refreshTokenRequest.getRefreshToken())){
            throw new InvalidTokenException("token is invalid");
        }

        Long userId= jwtUtil.getUserIdFromToken(refreshTokenRequest.getRefreshToken());

      refreshTokenRepository.findByRefTokenAndUserId(refreshTokenRequest.getRefreshToken(),userId).orElseThrow(()->new RefreshTokenNotFoundException("Refresh token not found"));
      refreshTokenRepository.deleteByRefTokenAndUserId(refreshTokenRequest.getRefreshToken(),userId);

      ApiResponse<Map<String,String>> apiResponse=ApiResponse.success(HttpStatus.CREATED.value(), Collections.emptyMap(),"User logged out successfully");
      return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    //generating access token and refresh token on valid refresh token and deleting old refresh token
    public ResponseEntity<ApiResponse<Map<String,String>>> refreshAccessToken(RefreshTokenRequest refreshTokenRequest){

          if(!jwtUtil.validateJwtToken(refreshTokenRequest.getRefreshToken())){
              throw new InvalidTokenException("token is invalid");
          }

          Long userId= jwtUtil.getUserIdFromToken(refreshTokenRequest.getRefreshToken());
          String email=jwtUtil.getUserNameFromJwtToken(refreshTokenRequest.getRefreshToken());

          RefreshToken oldRefreshToken=refreshTokenRepository.findByRefTokenAndUserId(refreshTokenRequest.getRefreshToken(),userId).orElseThrow(()->new RefreshTokenNotFoundException("Invalid refresh token already logged out"));

         if (oldRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(oldRefreshToken);
            throw new TokenExpiredException("Refresh token expired");
         }

          UserDetails userDetails=customUserDetailsService.loadUserByUsername(email);
          String accessToken=jwtUtil.generateJwtToken(userDetails,userId,accessTokenExpiration);
          String refreshToken=jwtUtil.generateJwtToken(userDetails,userId,refreshTokenExpiration);

          oldRefreshToken.setRefToken(refreshToken);
          oldRefreshToken.setCreatedAt(LocalDateTime.now());
          oldRefreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
          refreshTokenRepository.save(oldRefreshToken);

          Map<String,String> data=new HashMap<>();
          data.put("access_token",accessToken);
          data.put("refresh_token",refreshToken);

          ApiResponse<Map<String,String>> apiResponse=ApiResponse.success(HttpStatus.OK.value(), data,"New access and refresh tokens");
          return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    //generating password reset token for that user
    public ResponseEntity<ApiResponse<Map<String,String>>> forgotPassword(EmailRequest emailRequest){

        User user = userRepository.findByEmail(emailRequest.getEmail()).orElseThrow(()->new UserNotFoundException("User not found with this email"));

        //generate token
        String token=UUID.randomUUID().toString();
        String tokenHash=passwordEncoder.encode(token);

        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setTokenHash(tokenHash);
        verificationToken.setCreatedAt(LocalDateTime.now());
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(verificationTokenExpiry));
        verificationToken.setUsed(false);
        verificationToken.setTokenType(TokenType.FORGOT_PASSWORD);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);

        String resetLink = "http://localhost:8080/api/auth/reset-password?userId=" + user.getId() +
                "&&token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Reset your password");
        message.setText("Click the following link to reset your password:\n" + resetLink);
        javaMailSender.send(message);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), Collections.emptyMap(), "Password reset link sent"));
    }

    //verifying reset password token delete it from verification_token table and update new password
    public ResponseEntity<ApiResponse<Map<String,String>>> resetPassword(Long userId ,String token,String newPassword){

        VerificationToken verificationToken = verificationTokenRepository.findByUserIdAndTokenType(userId,TokenType.FORGOT_PASSWORD).orElseThrow(()->new VerificationTokenNotFoundException("Password reset token not found"));

        if(!passwordEncoder.matches(token,verificationToken.getTokenHash())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), Collections.emptyMap(), "Invalid token"));
        }

        if(verificationToken.getExpiresAt().isBefore(LocalDateTime.now())||verificationToken.isUsed()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), Collections.emptyMap(), "Token expired or already used"));
        }

        User user=verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        verificationTokenRepository.deleteByUserIdAndTokenType(userId,TokenType.FORGOT_PASSWORD);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), Collections.emptyMap(), "Password reset successful"));
    }

    //change password with the new password
    public ResponseEntity<ApiResponse<Map<String,String>>> changePassword(ChangePasswordRequest changePasswordRequest){
        if (Objects.equals(changePasswordRequest.getNewPassword(), changePasswordRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(
                            HttpStatus.BAD_REQUEST.value(),
                            Collections.emptyMap(),
                            "New password must be different from the current password"
                    )
            );
        }
        User user=userRepository.findById(changePasswordRequest.getUserId()).orElseThrow(()-> new UserNotFoundException("User not found with this userId"));
        if(!passwordEncoder.matches(changePasswordRequest.getPassword(),user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), Collections.emptyMap(), "Current password is incorrect"));
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), Collections.emptyMap(), "Password changed successfully"));
    }

}
