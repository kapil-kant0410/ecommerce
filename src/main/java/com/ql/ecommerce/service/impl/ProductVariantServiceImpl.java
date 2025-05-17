package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import com.ql.ecommerce.entity.Product;
import com.ql.ecommerce.entity.ProductVariant;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.exception.*;
import com.ql.ecommerce.mapper.ProductVariantMapper;
import com.ql.ecommerce.repository.ProductRepository;
import com.ql.ecommerce.repository.ProductVariantRepository;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.ProductVariantService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {

    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AuthUtil authUtil;
    private final ProductRepository productRepository;
    private final ProductVariantMapper productVariantMapper;

    public ProductVariantServiceImpl(ProductVariantMapper productVariantMapper,ProductVariantRepository productVariantRepository,ProductRepository productRepository,UserRepository userRepository,AuthUtil authUtil){
        this.authUtil=authUtil;
        this.userRepository=userRepository;
        this.productRepository=productRepository;
        this.productVariantRepository=productVariantRepository;
        this.productVariantMapper=productVariantMapper;
    }

    public ResponseEntity<ApiResponse<Map<String, ProductVariantDto>>> createProductVariant(ProductVariantDto productVariantDto){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFound("User not found with this email: "+email));

        Product product=productRepository.findById(productVariantDto.getProductId()).orElseThrow(()-> new ProductNotFound("product not found with this productId "+productVariantDto.getProductId()));

        if(!product.getUser().getId().equals(user.getId())){
            throw new Forbidden("You are not authorized to add this product variant.");
        }

        ProductVariant productVariant=productVariantMapper.toEntity(product,productVariantDto);
        productVariantRepository.save(productVariant);

        Map<String,ProductVariantDto> data=new HashMap<>();
        data.put("product_variant",productVariantDto);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), data,"Product variant created successfully"));
    }

    public ResponseEntity<ApiResponse<Map<String,ProductVariantDto>>> updateProductVariant(Long productVariantId,ProductVariantDto productVariantDto){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email "+email));
        ProductVariant productVariant=productVariantRepository.findById(productVariantId).orElseThrow(()-> new ProductVariantNotFound("Product variant not found with this product variant id"+productVariantId));

        if(!productVariant.getProduct().getUser().getId().equals(user.getId())){
            throw new Forbidden("You are not authorized to update this product variant");
        }

        productVariantMapper.updateEntity(productVariantDto,productVariant);
        productVariantRepository.save(productVariant);

        Map<String,ProductVariantDto> data=new HashMap<>();
        data.put("product_variant",productVariantDto);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), data,"Product variant updated successfully"));

    }

    public ResponseEntity<ApiResponse<Map<String,ProductVariantDto>>> deleteProductVariant(Long productVariantId) {
        String email = authUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with this email " + email));

        ProductVariant productVariant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new ProductVariantNotFound("Product variant not found with this product variant id" + productVariantId));

        if (!productVariant.getProduct().getUser().getId().equals(user.getId())) {
            throw new Forbidden("You are not authorized to delete this product variant");
        }

        ProductVariantDto productVariantDto=productVariantMapper.toDto(productVariant);
        productVariantRepository.delete(productVariant);

        Map<String,ProductVariantDto> data=new HashMap<>();
        data.put("product variant",productVariantDto);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                          data,
                "Product variant with ID " + productVariantId + " has been deleted"));
    }

}
