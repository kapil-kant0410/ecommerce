package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.LoginRequestDto;
import com.ql.ecommerce.dto.RegisterRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthService {
    ResponseEntity<ApiResponse<Map<String, String>>> register(RegisterRequestDto registerRequestDto);
    ResponseEntity<ApiResponse<Map<String, String>>> login(LoginRequestDto loginRequestDto);
}
