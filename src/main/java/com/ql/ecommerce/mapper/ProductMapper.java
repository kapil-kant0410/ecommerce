package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.product.response.ProductDto;
import com.ql.ecommerce.entity.Product;
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

    public Product toEntity(ProductDto productDto){
        return modelMapper.map(productDto,Product.class);
    }

    public List<Product> toEntityList(List<ProductDto> productDtos){
        return productDtos.stream().map(this::toEntity).toList();
    }

}
