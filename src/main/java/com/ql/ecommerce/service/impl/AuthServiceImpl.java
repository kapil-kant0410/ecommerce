package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.LoginRequestDto;
import com.ql.ecommerce.dto.RegisterRequestDto;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.enums.Role;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.JwtUtil;
import com.ql.ecommerce.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(PasswordEncoder passwordEncoder,UserRepository userRepository,AuthenticationManager authenticationManager,JwtUtil jwtUtil){
        this.authenticationManager=authenticationManager;
        this.jwtUtil=jwtUtil;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public ResponseEntity<ApiResponse<Map<String,String>>> register(RegisterRequestDto registerRequestDto){

        if(userRepository.existsByEmail(registerRequestDto.getEmail())){
            ApiResponse<Map<String,String>> apiResponse=  ApiResponse.error(HttpStatus.CONFLICT.value(), null,"Email already exists.");
            return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setName(registerRequestDto.getName());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setRole(Role.valueOf(registerRequestDto.getRole()));


        Map<String,String> data=new HashMap<>();
        data.put("user_name",user.getName());
        data.put("user_email",user.getEmail());

        userRepository.save(user);

        ApiResponse<Map<String,String>> apiResponse=ApiResponse.success(HttpStatus.CREATED.value(), data,"User registered");
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);


    }

    public ResponseEntity<ApiResponse<Map<String,String>>> login(LoginRequestDto loginRequestDto){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        Map<String,String> data=new HashMap<>();
        data.put("Jwt_token",jwt);

        ApiResponse<Map<String,String>> apiResponse=ApiResponse.success(HttpStatus.CREATED.value(), data,"User Logged In");
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

}
