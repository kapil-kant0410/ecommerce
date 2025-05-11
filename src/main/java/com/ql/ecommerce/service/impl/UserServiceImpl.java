package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.user.request.UserUpdate;
import com.ql.ecommerce.dto.user.response.UserDto;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.exception.UserNotFoundException;
import com.ql.ecommerce.mapper.UserMapper;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper,UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMapper=userMapper;
    }

    public ResponseEntity<ApiResponse<Map<String,List<UserDto>>>> getAllUsers() {

        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = userMapper.toDtoList(users);

        Map<String, List<UserDto>> data = new HashMap<>();
        data.put("users", userDtos);
        
        ApiResponse<Map<String, List<UserDto>>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "All users fetched successfully"
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<Map<String,UserDto>>> getCurrentUser(){

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null||!authentication.isAuthenticated()){
            ApiResponse<Map<String, UserDto>> response = ApiResponse.error(
                    HttpStatus.UNAUTHORIZED.value(),
                    null,
                    "Unauthorized access"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        UserDto userDto=userMapper.toDto(user);

        Map<String, UserDto> data = new HashMap<>();
        data.put("user", userDto);

        ApiResponse<Map<String, UserDto>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "Current user profile fetched successfully"
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<Map<String,UserDto>>> updateCurrentUser(UserUpdate userUpdate){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            ApiResponse<Map<String, UserDto>> response = ApiResponse.error(
                    HttpStatus.UNAUTHORIZED.value(),
                    null,
                    "Unauthorized access"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        user.setName(userUpdate.getName());
        User updatedUser=userRepository.save(user);
        UserDto userDto=userMapper.toDto(updatedUser);

        Map<String, UserDto> data = new HashMap<>();
        data.put("user", userDto);

        ApiResponse<Map<String, UserDto>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "User profile updated successfully"
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<Map<String, String>>> deleteCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            ApiResponse<Map<String, String>> response = ApiResponse.error(
                    HttpStatus.UNAUTHORIZED.value(),
                    null,
                    "Unauthorized access"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        user.setEnabled(false);

        userRepository.save(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "User account deleted (soft delete)");

        ApiResponse<Map<String, String>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "Account deactivated successfully"
        );

        return ResponseEntity.ok(response);
    }

}