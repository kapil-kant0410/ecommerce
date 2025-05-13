package com.ql.ecommerce.controller;


import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.product.response.ProductDto;
import com.ql.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Map<String, ProductDto>>> createProduct(@Valid @RequestBody ProductDto productDto){
          return productService.createProduct(productDto);
    }

    @GetMapping()
    ResponseEntity<ApiResponse<Map<String, List<ProductDto>>>> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Map<String,ProductDto>>> getProductById(@PathVariable Long productId){
        return productService.getProductById(productId);
    }

}
