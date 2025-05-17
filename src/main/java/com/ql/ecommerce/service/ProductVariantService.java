package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ProductVariantService {
     ResponseEntity<ApiResponse<Map<String, ProductVariantDto>>> createProductVariant(ProductVariantDto productVariantDto);
     ResponseEntity<ApiResponse<Map<String,ProductVariantDto>>> updateProductVariant(Long productVariantId,ProductVariantDto productVariantDto);
     ResponseEntity<ApiResponse<Map<String,ProductVariantDto>>> deleteProductVariant(Long productVariantId);
}

