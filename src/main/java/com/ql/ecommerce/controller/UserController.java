package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<Map<String, List<User>>>> getAllUsers(){
        return userService.getAllUsers();
    }



}
