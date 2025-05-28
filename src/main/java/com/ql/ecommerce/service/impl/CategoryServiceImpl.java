package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.category.response.CategoryDto;
import com.ql.ecommerce.entity.Category;
import com.ql.ecommerce.exception.ResourceNotFound;
import com.ql.ecommerce.mapper.CategoryMapper;
import com.ql.ecommerce.repository.CategoryRepository;
import com.ql.ecommerce.service.CategoryService;
import com.ql.ecommerce.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ResponseBuilder responseBuilder;

    public CategoryServiceImpl(ResponseBuilder responseBuilder,CategoryMapper categoryMapper,CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
        this.categoryMapper=categoryMapper;
        this.responseBuilder=responseBuilder;
    }

    public ResponseEntity<ApiResponse<Map<String,Object>>> getAllCategories(){
          List<Category> categories=categoryRepository.findAll();
          List<CategoryDto> categoryDtos=categoryMapper.toDtoList(categories);

          return responseBuilder.build("Category",categoryDtos,"Categories fetched successfully");
    }

    public ResponseEntity<ApiResponse<Map<String,Object>>> getCategoryById(Long categoryId){
        Category category=categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFound("Category not found with id:"+categoryId));
        CategoryDto categoryDto=categoryMapper.toDto(category);

        return responseBuilder.build("Category",categoryDto,"Category fetched successfully");
    }

}
