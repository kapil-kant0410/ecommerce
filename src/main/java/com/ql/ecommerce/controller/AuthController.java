package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.LoginRequestDto;
import com.ql.ecommerce.dto.RegisterRequestDto;
import com.ql.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String,String>>> registerUser(@Valid @RequestBody RegisterRequestDto userRegisterRequestDto){
        return authService.register(userRegisterRequestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,String>>> loginByPassword(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }


}
