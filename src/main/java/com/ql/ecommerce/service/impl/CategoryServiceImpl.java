package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.category.response.CategoryDto;
import com.ql.ecommerce.entity.Category;
import com.ql.ecommerce.exception.ResourceNotFoundException;
import com.ql.ecommerce.mapper.CategoryMapper;
import com.ql.ecommerce.repository.CategoryRepository;
import com.ql.ecommerce.service.CategoryService;
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

    public CategoryServiceImpl(CategoryMapper categoryMapper,CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
        this.categoryMapper=categoryMapper;
    }

    public ResponseEntity<ApiResponse<Map<String,List<CategoryDto>>>> getAllCategories(){

          List<Category> categories=categoryRepository.findAll();
          List<CategoryDto> categoryDtos=categoryMapper.toDtoList(categories);

          Map<String, List<CategoryDto>> data=new HashMap<>();
          data.put("category",categoryDtos);

          return ResponseEntity.ok( ApiResponse.success(HttpStatus.OK.value(),data,"Categories fetched successfully"));
    }

    public ResponseEntity<ApiResponse<Map<String,CategoryDto>>> getCategoryById(Long categoryId){

        Category category=categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Category not found with id:"+categoryId));

        CategoryDto categoryDto=categoryMapper.toDto(category);

        Map<String,CategoryDto> data=new HashMap<>();
        data.put("category",categoryDto);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), data, "Category fetched successfully"));
    }


}
