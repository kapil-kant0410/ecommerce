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
     ResponseEntity<ApiResponse<Map<String, ProductDto>>> createProduct(ProductDto productDto);
     ResponseEntity<ApiResponse<Map<String, List<ProductDto>>>> getAllProducts();
     ResponseEntity<ApiResponse<Map<String,Object>>> getAllProductsV1(ProductFilter productFilter);
     ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByCategory(Long productId);
     ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByBrand(Long productId);
     ResponseEntity<ApiResponse<Map<String,ProductDto>>> getProductById(Long productId);
     ResponseEntity<ApiResponse<Map<String,ProductDto>>> updateProduct(Long productId,ProductDto productDto);
     ResponseEntity<ApiResponse<Map<String,ProductDto>>> deleteProduct(Long productId);
     ResponseEntity<ApiResponse<Map<String, List<ProductVariantDto>>>> getProductVariantsByProductId(Long productId);
     ResponseEntity<ApiResponse<Map<String, Object>>> getProductVariantsByProductIdV1(ProductVariantFilter filter);
     ResponseEntity<ApiResponse<Map<String,Object>>> getOtherVariantsByProductVariantId(Long productVariantId);
     ResponseEntity<ApiResponse<Map<String, ProductVariantDto>>> getProductVariantByVariantId(Long productId, Long productVariantId);
}
