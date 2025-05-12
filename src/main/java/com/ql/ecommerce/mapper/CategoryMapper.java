package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.category.response.CategoryDto;
import com.ql.ecommerce.entity.Category;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    private final ModelMapper modelMapper;

    public CategoryMapper(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }

   public CategoryDto toDto(Category category){
        return modelMapper.map(category,CategoryDto.class);
   }

   public List<CategoryDto> toDtoList(List<Category> categories){
        return categories.stream().map(this::toDto).toList();
   }

   public Category toEntity(CategoryDto categoryDto){
           return  modelMapper.map(categoryDto,Category.class);
   }

   public List<Category> toEntityList(List<CategoryDto> categoryDtos){
        return categoryDtos.stream().map(this::toEntity).toList();
   }

}
