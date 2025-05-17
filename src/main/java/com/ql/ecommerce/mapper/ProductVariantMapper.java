package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import com.ql.ecommerce.entity.Product;
import com.ql.ecommerce.entity.ProductVariant;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductVariantMapper {

    private final ModelMapper modelMapper;

    public ProductVariantMapper(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }

    public ProductVariantDto toDto(ProductVariant productVariant){
        return modelMapper.map(productVariant, ProductVariantDto.class);
    }

    public List<ProductVariantDto> toDtoList(List<ProductVariant> productVariants){
        return productVariants.stream().map(this::toDto).toList();
    }

    public ProductVariant toEntity(Product product,ProductVariantDto productVariantDto){
        return ProductVariant.builder()
                .sku(productVariantDto.getSku())
                .size(productVariantDto.getSize())
                .color(productVariantDto.getColor())
                .price(productVariantDto.getPrice())
                .rating(productVariantDto.getRating())
                .stockQuantity(productVariantDto.getStockQuantity())
                .reservedQuantity(productVariantDto.getReservedQuantity())
                .product(product)
                .active(true)
                .build();
    }

    public void updateEntity(ProductVariantDto productVariantDto,ProductVariant productVariant){
        productVariant.setSku(productVariantDto.getSku());
        productVariant.setSize(productVariantDto.getSize());
        productVariant.setColor(productVariantDto.getColor());
        productVariant.setPrice(productVariantDto.getPrice());
        productVariant.setRating(productVariantDto.getRating());
        productVariant.setStockQuantity(productVariantDto.getStockQuantity());
        productVariant.setReservedQuantity(productVariantDto.getReservedQuantity());
    }


}
