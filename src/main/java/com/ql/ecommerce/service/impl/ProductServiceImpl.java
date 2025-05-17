package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import com.ql.ecommerce.dto.ProductVariant.filter.ProductVariantFilter;
import com.ql.ecommerce.dto.product.filter.ProductFilter;
import com.ql.ecommerce.dto.product.response.ProductDto;
import com.ql.ecommerce.entity.Category;
import com.ql.ecommerce.entity.Product;
import com.ql.ecommerce.entity.ProductVariant;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.enums.Role;
import com.ql.ecommerce.exception.*;
import com.ql.ecommerce.mapper.ProductMapper;
import com.ql.ecommerce.mapper.ProductVariantMapper;
import com.ql.ecommerce.repository.CategoryRepository;
import com.ql.ecommerce.repository.ProductRepository;
import com.ql.ecommerce.repository.ProductVariantRepository;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.ProductService;
import com.ql.ecommerce.specification.ProductSpecification;
import com.ql.ecommerce.specification.ProductVariantSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final ProductVariantRepository productVariantRepository;
    private final ProductMapper productMapper;
    private final ProductVariantMapper productVariantMapper;
    private final ProductSpecification productSpecification;
    private final ProductVariantSpecification productVariantSpecification;

    public ProductServiceImpl(ProductVariantSpecification productVariantSpecification,ProductSpecification productSpecification,ProductVariantMapper productVariantMapper,ProductVariantRepository productVariantRepository,ProductMapper productMapper,ProductRepository productRepository,UserRepository userRepository,AuthUtil authUtil,CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
        this.authUtil=authUtil;
        this.userRepository=userRepository;
        this.productRepository=productRepository;
        this.productMapper=productMapper;
        this.productVariantRepository=productVariantRepository;
        this.productVariantMapper=productVariantMapper;
        this.productSpecification=productSpecification;
        this.productVariantSpecification=productVariantSpecification;
    }

    public ResponseEntity<ApiResponse<Map<String, ProductDto>>> createProduct(ProductDto productDto){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFound("User not found with this email: "+email));
        Category category=categoryRepository.findById(productDto.getCategoryId()).orElseThrow(()-> new CategoryNotFound("Category not found with id:"+productDto.getCategoryId()));

        if(!user.getRole().equals(Role.ROLE_SELLER)){
              throw new Forbidden("Only sellers are allowed to perform this action.");
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

    //page (default: 0)
    //page number starts from 0
    //size (default: 5)
    //sort (e.g., "name,asc", "createdAt,desc")
    //category (optional) (supports) (id,slug)
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAllProductsV1(ProductFilter productFilter){
            Sort sort=productFilter.getDirection().equalsIgnoreCase("asc")?Sort.by(productFilter.getSortBy()).ascending():Sort.by(productFilter.getSortBy()).descending();
            Pageable pageable = PageRequest.of(productFilter.getPage(), productFilter.getSize(), sort);
            Specification<Product> spec = Specification
                .where(productSpecification.hasCategory(productFilter.getCategory()))
                .and(productSpecification.hasBrandIn(productFilter.getBrands()));

        Page<Product> productPage = productRepository.findAll(spec,pageable);
        List<ProductDto> productDtos=productPage.getContent().stream().map(productMapper::toDto).toList();

        Map<String, Object> data = new HashMap<>();
        data.put("products", productDtos);
        data.put("currentPage", productPage.getNumber());
        data.put("totalPages", productPage.getTotalPages());
        data.put("totalItems", productPage.getTotalElements());
        data.put("pageSize", productPage.getSize());
        data.put("isLastPage", productPage.isLast());

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), data, "Products fetched successfully"));
    }

    public ResponseEntity<ApiResponse<Map<String,ProductDto>>> getProductById(Long productId){
        Product product=productRepository.findById(productId).orElseThrow(()-> new ProductNotFound("Product not found with this productId: "+productId));
        ProductDto productDto=productMapper.toDto(product);

        Map<String,ProductDto> data=new HashMap<>();
        data.put("product",productDto);

        return ResponseEntity.ok(ApiResponse.error(HttpStatus.OK.value(),data,"Product fetched successfully"));
    }

    public ResponseEntity<ApiResponse<Map<String,ProductDto>>> updateProduct(Long productId, ProductDto productDto){

        String email= authUtil.getCurrentUserEmail();
        Product product=productRepository.findById(productId).orElseThrow(()-> new ProductNotFound("Product not found with this productId "+productId));
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email "+email));

        if(!product.getUser().getId().equals(user.getId())){
             throw new Forbidden("You are not authorized to update this product.");
        }

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFound("Category not found with id: " + productDto.getCategoryId()));


        product.setName(productDto.getName());
        product.setShortDescription(productDto.getShortDescription());
        product.setFullDescription(productDto.getFullDescription());
        product.setBrand(productDto.getBrand());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        ProductDto updatedProductDto = productMapper.toDto(updatedProduct);


        Map<String, ProductDto> data = new HashMap<>();
        data.put("product", updatedProductDto);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), data, "Product updated successfully"));

    }

    public ResponseEntity<ApiResponse<Map<String,ProductDto>>> deleteProduct(Long productId) {

        String email = authUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with this email: " + email));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product not found with id: " + productId));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new Forbidden("You are not authorized to delete this product.");
        }

        ProductDto productDto=productMapper.toDto(product);
        Map<String,ProductDto> data=new HashMap<>();
        data.put("product",productDto);

        productRepository.delete(product);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), data, "Product deleted successfully"
        ));

    }

    public ResponseEntity<ApiResponse<Map<String, List<ProductVariantDto>>>> getProductVariantsByProductId(Long productId ){

        Product product=productRepository.findById(productId).orElseThrow(()-> new ProductNotFound("product not found with this productId "+productId));

        List<ProductVariant> productVariants=product.getVariants();
        List<ProductVariantDto> productVariantDtos=productVariantMapper.toDtoList(productVariants);

        Map<String,List<ProductVariantDto>> data=new HashMap<>();
        data.put("product variants",productVariantDtos);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),data,"Product variants fetched successfully"));

    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductVariantsByProductIdV1( ProductVariantFilter filter ){

        Sort sort = filter.getDirection().equalsIgnoreCase("asc") ? Sort.by(filter.getSortBy()).ascending() : Sort.by(filter.getSortBy()).descending();
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);


        Specification<ProductVariant> spec = Specification
                .where(productVariantSpecification.hasColorIn(filter.getColors()))
                .and(productVariantSpecification.hasSizeIn(filter.getSizes()))
                .and(productVariantSpecification.hasPriceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(productVariantSpecification.hasMinimumRating(filter.getRating()));

        Page<ProductVariant> pageResult = productVariantRepository.findAll(spec, pageable);
        List<ProductVariantDto> productVariantDtos= pageResult.getContent().stream()
                .map(productVariantMapper::toDto)
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("variants", productVariantDtos);
        data.put("currentPage", pageResult.getNumber());
        data.put("totalPages", pageResult.getTotalPages());
        data.put("totalItems", pageResult.getTotalElements());
        data.put("pageSize", pageResult.getSize());
        data.put("isLastPage", pageResult.isLast());

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), data, "Product Variants fetched successfully"));
    }

    public ResponseEntity<ApiResponse<Map<String, ProductVariantDto>>> getProductVariantByVariantId(Long productId, Long productVariantId){

        ProductVariant productVariant=productVariantRepository.findById(productVariantId).orElseThrow(()->new ProductVariantNotFound("Product variant not found with this is "+productVariantId));

        if (!productVariant.getProduct().getId().equals(productId)) {
            throw new BadRequest("This variant does not belong to the given product.");
        }

        ProductVariantDto productVariantDto=productVariantMapper.toDto(productVariant);

        Map<String,ProductVariantDto> data=new HashMap<>();
        data.put("product variant",productVariantDto);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), data, "Product variant fetched successfully"));
    }

}
