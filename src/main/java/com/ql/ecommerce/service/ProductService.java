package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import com.ql.ecommerce.dto.ProductVariant.filter.ProductVariantFilter;
import com.ql.ecommerce.dto.product.filter.ProductFilter;
import com.ql.ecommerce.dto.product.response.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ProductService {
     ResponseEntity<ApiResponse<Map<String,Object>>> createProduct(ProductDto productDto);
     ResponseEntity<ApiResponse<Map<String,Object>>> getAllProducts();
     ResponseEntity<ApiResponse<Map<String,Object>>> getAllProductsV1(ProductFilter productFilter);
     ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByCategory(Long productId);
     ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByBrand(Long productId);
     ResponseEntity<ApiResponse<Map<String,Object>>> getProductById(Long productId);
     ResponseEntity<ApiResponse<Map<String,Object>>> getRecentlyViewedProducts();
     ResponseEntity<ApiResponse<Map<String,Object>>> updateProduct(Long productId,ProductDto productDto);
     ResponseEntity<ApiResponse<Map<String,Object>>> deleteProduct(Long productId);
     ResponseEntity<ApiResponse<Map<String,Object>>> getProductVariantsByProductId(Long productId);
     ResponseEntity<ApiResponse<Map<String,Object>>> getProductVariantsByProductIdV1(ProductVariantFilter filter);
     ResponseEntity<ApiResponse<Map<String,Object>>> getOtherVariantsByProductVariantId(Long productVariantId);
     ResponseEntity<ApiResponse<Map<String,Object>>> getProductVariantByVariantId(Long productId, Long productVariantId);
     ResponseEntity<ApiResponse<Map<String,Object>>> getRecentlyViewedProductVariants();
}
