package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.user.request.UserUpdate;
import com.ql.ecommerce.dto.user.response.UserDto;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Map<String, List<UserDto>>>> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, UserDto>>> getCurrentUser(){
        return userService.getCurrentUser();
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Map<String,UserDto>>> updateCurrentUser(@Valid @RequestBody UserUpdate userUpdate){
        return userService.updateCurrentUser(userUpdate);
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteCurrentUser(){
        return userService.deleteCurrentUser();
    }


}
