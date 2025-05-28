package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.product.response.ProductDto;
import com.ql.ecommerce.entity.Category;
import com.ql.ecommerce.entity.Product;
import com.ql.ecommerce.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }

    public ProductDto toDto(Product product){
        return modelMapper.map(product,ProductDto.class);
    }

    public List<ProductDto> toDtoList(List<Product> products){
        return products.stream().map(this::toDto).toList();
    }

    public Product toEntity(ProductDto productDto, Category category, User user){
        return  Product.builder()
                .name(productDto.getName())
                .shortDescription(productDto.getShortDescription())
                .fullDescription(productDto.getFullDescription())
                .brand(productDto.getBrand())
                .category(category)
                .user(user)
                .build();
    }

    public void updateProduct(Product product,ProductDto productDto,Category category){
        product.setName(productDto.getName());
        product.setShortDescription(productDto.getShortDescription());
        product.setFullDescription(productDto.getFullDescription());
        product.setBrand(productDto.getBrand());
        product.setCategory(category);
    }


}
