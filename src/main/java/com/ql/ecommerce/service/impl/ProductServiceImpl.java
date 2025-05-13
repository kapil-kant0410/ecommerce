package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.product.response.ProductDto;
import com.ql.ecommerce.entity.Category;
import com.ql.ecommerce.entity.Product;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.enums.Role;
import com.ql.ecommerce.exception.CategoryNotFoundException;
import com.ql.ecommerce.exception.ForbiddenException;
import com.ql.ecommerce.exception.ProductNotFoundException;
import com.ql.ecommerce.exception.UserNotFoundException;
import com.ql.ecommerce.mapper.ProductMapper;
import com.ql.ecommerce.repository.CategoryRepository;
import com.ql.ecommerce.repository.ProductRepository;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuthUtil authUtil;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper,ProductRepository productRepository,UserRepository userRepository,AuthUtil authUtil,CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
        this.authUtil=authUtil;
        this.userRepository=userRepository;
        this.productRepository=productRepository;
        this.productMapper=productMapper;
    }

    public ResponseEntity<ApiResponse<Map<String, ProductDto>>> createProduct(ProductDto productDto){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found with this email: "+email));
        Category category=categoryRepository.findById(productDto.getCategoryId()).orElseThrow(()-> new CategoryNotFoundException("Category not found with id:"+productDto.getCategoryId()));

        if(!user.getRole().equals(Role.ROLE_SELLER)){
              throw new ForbiddenException("Only sellers are allowed to perform this action.");
        }

      Product product= Product.builder()
              .name(productDto.getName())
              .shortDescription(productDto.getShortDescription())
              .fullDescription(productDto.getFullDescription())
              .brand(productDto.getBrand())
              .category(category)
              .user(user)
              .build();

      Product createdProduct = productRepository.save(product);
      ProductDto createdProductDto =productMapper.toDto(createdProduct);

      Map<String,ProductDto> data=new HashMap<>();
      data.put("product",createdProductDto );

      return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), data,"Product created successfully"));
    }

    public ResponseEntity<ApiResponse<Map<String,List<ProductDto>>>> getAllProducts(){
        List<Product> products=productRepository.findAll();
        List<ProductDto> productDtos=productMapper.toDtoList(products);

        Map<String,List<ProductDto>> data=new HashMap<>();
        data.put("products",productDtos);

        return ResponseEntity.ok(ApiResponse.error(HttpStatus.OK.value(),data,"Products fetched successfully"));
    }

    public ResponseEntity<ApiResponse<Map<String,ProductDto>>> getProductById(Long productId){
        Product product=productRepository.findById(productId).orElseThrow(()-> new ProductNotFoundException("Product not found with this productId: "+productId));
        ProductDto productDto=productMapper.toDto(product);

        Map<String,ProductDto> data=new HashMap<>();
        data.put("product",productDto);

        return ResponseEntity.ok(ApiResponse.error(HttpStatus.OK.value(),data,"Product fetched successfully"));
    }

//    public ResponseEntity<ApiResponse<Map<String,ProductDto>>> updateProduct(Long productId,ProductDto productDto){
//
//        Product product=productRepository.findById(productId).orElseThrow(()-> new ProductNotFoundException("Product not found with this productId "+productId));
//
//
//
//
//    }






}
