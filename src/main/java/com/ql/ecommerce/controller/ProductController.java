package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import com.ql.ecommerce.dto.ProductVariant.filter.ProductVariantFilter;
import com.ql.ecommerce.dto.product.filter.ProductFilter;
import com.ql.ecommerce.dto.product.response.ProductDto;
import com.ql.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Default;
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

    @GetMapping("/v1")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAllProductsV1(@ModelAttribute ProductFilter productFilter){
        return productService.getAllProductsV1(productFilter);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Map<String,ProductDto>>> getProductById(@PathVariable Long productId){
        return productService.getProductById(productId);
    }

    @PutMapping("/{productId}")
    ResponseEntity<ApiResponse<Map<String,ProductDto>>> updateProduct(@PathVariable Long productId,@RequestBody @Valid ProductDto productDto){
        return productService.updateProduct(productId,productDto);
    }

    @DeleteMapping("/{productId}")
    ResponseEntity<ApiResponse<Map<String,ProductDto>>> deleteProduct(@PathVariable Long productId){
        return productService.deleteProduct(productId);
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<ApiResponse<Map<String, List<ProductVariantDto>>>> getProductVariantsByProductId(@PathVariable Long productId){
        return productService.getProductVariantsByProductId(productId);
    }

    @GetMapping("/{productId}/variants/v1")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductVariantsByProductIdV1(
            @PathVariable Long productId,
            @ModelAttribute ProductVariantFilter filter) {

        return productService.getProductVariantsByProductIdV1(filter);
    }

    @GetMapping("/{productId}/product-variant/{productVariantId}")
    public ResponseEntity<ApiResponse<Map<String, ProductVariantDto>>> getProductVariantByVariantId(@PathVariable Long productId,@PathVariable Long productVariantId){
           return productService.getProductVariantByVariantId(productId,productVariantId);
    }

}
