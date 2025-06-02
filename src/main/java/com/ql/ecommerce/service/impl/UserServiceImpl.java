package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.user.request.UserUpdate;
import com.ql.ecommerce.dto.user.response.UserDto;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.enums.Role;
import com.ql.ecommerce.exception.BadRequest;
import com.ql.ecommerce.exception.Unauthorized;
import com.ql.ecommerce.mapper.UserMapper;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.UserService;
import com.ql.ecommerce.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ResponseBuilder responseBuilder;
    private final AuthUtil authUtil;

    public UserServiceImpl(AuthUtil authUtil,ResponseBuilder responseBuilder,UserMapper userMapper,UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMapper=userMapper;
        this.responseBuilder=responseBuilder;
        this.authUtil=authUtil;
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllUsers() {
        User user=authUtil.getCurrentUser();
        if(!user.getRole().equals(Role.ROLE_ADMIN)){
            throw new Unauthorized("Only admin is allowed to access it");
        }
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = userMapper.toDtoList(users);
        return responseBuilder.build("users", userDtos, "All users fetched successfully");
    }

    public ResponseEntity<ApiResponse<Map<String,Object>>> getCurrentUser(){
        User user=authUtil.getCurrentUser();
        UserDto userDto = userMapper.toDto(user);
        return responseBuilder.build("user", userDto, "Current user profile fetched successfully");
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> updateCurrentUser(UserUpdate userUpdate) {
        User user=authUtil.getCurrentUser();
        user.setName(userUpdate.getName());
        User updatedUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(updatedUser);

        return responseBuilder.build("user", userDto, "User profile updated successfully");
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteCurrentUser() {
        User user=authUtil.getCurrentUser();
        user.setEnabled(false);
        userRepository.save(user);

        return responseBuilder.build(Collections.emptyMap(), "User account deleted (soft delete)");
    }

}