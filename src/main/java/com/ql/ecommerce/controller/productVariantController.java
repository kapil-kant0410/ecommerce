package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import com.ql.ecommerce.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/product-variant")
public class productVariantController {

    private final ProductVariantService productVariantService;

    public productVariantController(ProductVariantService productVariantService){
        this.productVariantService=productVariantService;
    }

    @PostMapping()
    ResponseEntity<ApiResponse<Map<String, ProductVariantDto>>> createProductVariant(@RequestBody @Valid ProductVariantDto productVariantDto){
       return productVariantService.createProductVariant(productVariantDto);
    }

    @PutMapping("/{productVariantId}")
    ResponseEntity<ApiResponse<Map<String,ProductVariantDto>>> updateProductVariant(@PathVariable Long productVariantId,@RequestBody @Valid ProductVariantDto productVariantDto){
        return productVariantService.updateProductVariant(productVariantId,productVariantDto);
    }

    @DeleteMapping("/{productVariantId}")
    ResponseEntity<ApiResponse<Map<String,ProductVariantDto>>> deleteProductVariant(@PathVariable Long productVariantId){
        return productVariantService.deleteProductVariant(productVariantId);
    }

}
