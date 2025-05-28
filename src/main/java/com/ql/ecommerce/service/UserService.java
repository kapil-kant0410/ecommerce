package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.user.request.UserUpdate;
import com.ql.ecommerce.dto.user.response.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UserService {
    ResponseEntity<ApiResponse<Map<String,Object>>> getAllUsers();
    ResponseEntity<ApiResponse<Map<String,Object>>> getCurrentUser();
    ResponseEntity<ApiResponse<Map<String, Object>>> updateCurrentUser(UserUpdate userUpdate);
    ResponseEntity<ApiResponse<Map<String, Object>>> deleteCurrentUser();
}