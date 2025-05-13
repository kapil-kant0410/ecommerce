package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.product.response.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ProductService {

     ResponseEntity<ApiResponse<Map<String, ProductDto>>> createProduct(ProductDto productDto);
     ResponseEntity<ApiResponse<Map<String, List<ProductDto>>>> getAllProducts();
     ResponseEntity<ApiResponse<Map<String,ProductDto>>> getProductById(Long productId);
}
