package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, List<User>>>> getAllUsers() {
        List<User> allUsers = userRepository.findAll();

        Map<String, List<User>> data = new HashMap<>();
        data.put("users", allUsers);  // Changed key to lowercase for consistency

        ApiResponse<Map<String, List<User>>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "All users fetched successfully"
        );

        return ResponseEntity.ok(response);
    }

}