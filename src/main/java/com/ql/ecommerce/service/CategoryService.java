package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface CategoryService {
    ResponseEntity<ApiResponse<Map<String,Object>>> getAllCategories();
    ResponseEntity<ApiResponse<Map<String,Object>>> getCategoryById(Long categoryId);
}
