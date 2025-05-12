package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.category.response.CategoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CategoryService {

    ResponseEntity<ApiResponse<Map<String, List<CategoryDto>>>> getAllCategories();
    ResponseEntity<ApiResponse<Map<String,CategoryDto>>> getCategoryById(Long categoryId);

}
