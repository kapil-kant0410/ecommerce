package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import com.ql.ecommerce.dto.product.response.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecentlyViewedService {
    void addToRecentlyViewedProducts(Long userId, ProductDto productDto);
    List<ProductDto> getRecentlyViewedProducts(Long userId);
    void addToRecentlyViewedVariants(Long userId, ProductVariantDto productVariantDto);
    List<ProductVariantDto> getRecentlyViewedVariants(Long userId);
}
