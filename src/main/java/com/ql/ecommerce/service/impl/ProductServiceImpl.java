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
import com.ql.ecommerce.service.RecentlyViewedService;
import com.ql.ecommerce.specification.ProductSpecification;
import com.ql.ecommerce.specification.ProductVariantSpecification;
import com.ql.ecommerce.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private final RecentlyViewedService recentlyViewedService;
    private final ResponseBuilder responseBuilder;
    private final Logger logger= LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ResponseBuilder responseBuilder,RecentlyViewedService recentlyViewedService,ProductVariantSpecification productVariantSpecification,ProductSpecification productSpecification,ProductVariantMapper productVariantMapper,ProductVariantRepository productVariantRepository,ProductMapper productMapper,ProductRepository productRepository,UserRepository userRepository,AuthUtil authUtil,CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
        this.authUtil=authUtil;
        this.userRepository=userRepository;
        this.productRepository=productRepository;
        this.productMapper=productMapper;
        this.productVariantRepository=productVariantRepository;
        this.productVariantMapper=productVariantMapper;
        this.productSpecification=productSpecification;
        this.productVariantSpecification=productVariantSpecification;
        this.recentlyViewedService=recentlyViewedService;
        this.responseBuilder=responseBuilder;
    }

    public ResponseEntity<ApiResponse<Map<String,Object>>> createProduct(ProductDto productDto){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFound("User not found with this email: "+email));
        Category category=categoryRepository.findById(productDto.getCategoryId()).orElseThrow(()-> new CategoryNotFound("Category not found with id:"+productDto.getCategoryId()));

        if(!user.getRole().equals(Role.ROLE_SELLER)){
              throw new Forbidden("Only sellers are allowed to perform this action.");
        }

      Product product= productMapper.toEntity(productDto,category,user);

      Product createdProduct = productRepository.save(product);
      ProductDto createdProductDto =productMapper.toDto(createdProduct);

      return responseBuilder.build("product",createdProductDto,"Product created successfully");

    }

    //page (default: 0)
    //page number starts from 0
    //size (default: 5)
    //sort (e.g., "name,asc", "createdAt,desc")
    //category (optional) (supports) (id,slug)
    //brands (optional)
    //This method retrieves a paginated, sorted, and filtered list of products based on the criteria provided in a ProductFilter object.
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAllProducts(ProductFilter productFilter){
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

        return responseBuilder.build(data,"Products fetched successfully");
    }

    //: Fetches up to 10 similar products that belong to the same category as the given product, excluding the product itself.
    public ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByCategory(Long productId){

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product not found with this "+ productId));

        List<Product> similarProducts = productRepository.findTop10ByCategoryIdAndIdNot(
                product.getCategory().getId(), productId);

        List<ProductDto> similarProductDtos=productMapper.toDtoList(similarProducts);

        return responseBuilder.build("similar products",similarProductDtos,"Similar products by category fetched successfully");

    }

    //: Fetches up to 10 similar products that belong to the same brand as the given product, excluding the product itself.
    public ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByBrand(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product not found with this "+ productId));

        List<Product> similarProducts = productRepository.findTop10ByBrandAndIdNot(
                product.getBrand(), productId);

        List<ProductDto> similarProductDtos=productMapper.toDtoList(similarProducts);

        return responseBuilder.build("similar products",similarProductDtos,"Similar products by brand fetched successfully");

    }

    //: Returns product details for the given ID and stores it in the user's recently viewed list.
    public ResponseEntity<ApiResponse<Map<String,Object>>> getProductById(Long productId){
        Product product=productRepository.findById(productId).orElseThrow(()-> new ProductNotFound("Product not found with this productId: "+productId));
        ProductDto productDto=productMapper.toDto(product);

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email "+email));

        recentlyViewedService.addToRecentlyViewedProducts(user.getId(), productDto);

        return responseBuilder.build("product",productDto,"Product fetched successfully");

    }

    //Retrieves the list of products recently viewed by the current logged-in user.
    public ResponseEntity<ApiResponse<Map<String,Object>>> getRecentlyViewedProducts(){
        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email "+email));
        List<ProductDto> productDtos=recentlyViewedService.getRecentlyViewedProducts(user.getId());
        return responseBuilder.build("products",productDtos,"All recently viewed products");
    }

    //Allows the product owner to update the product details.
    public ResponseEntity<ApiResponse<Map<String,Object>>> updateProduct(Long productId, ProductDto productDto){

        String email= authUtil.getCurrentUserEmail();
        Product product=productRepository.findById(productId).orElseThrow(()-> new ProductNotFound("Product not found with this productId "+productId));
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email "+email));

        if(!product.getUser().getId().equals(user.getId())){
             throw new Forbidden("You are not authorized to update this product.");
        }

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFound("Category not found with id: " + productDto.getCategoryId()));

        productMapper.updateProduct(product,productDto,category);

        Product updatedProduct = productRepository.save(product);
        ProductDto updatedProductDto = productMapper.toDto(updatedProduct);

        return responseBuilder.build("product",updatedProductDto,"Product updated successfully");

    }

    //Allows the product owner to delete the product.
    public ResponseEntity<ApiResponse<Map<String,Object>>> deleteProduct(Long productId) {

        String email = authUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with this email: " + email));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product not found with id: " + productId));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new Forbidden("You are not authorized to delete this product.");
        }

        productRepository.delete(product);

        return responseBuilder.build(Collections.emptyMap(),"Product deleted successfully");
    }


    //Handling product variants


    //Retrieves a paginated, sorted, and filtered list of product variants based on color, size, price, and rating.
    public ResponseEntity<ApiResponse<Map<String,Object>>> getProductVariants( ProductVariantFilter filter){

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

        return responseBuilder.build(data,"Product Variants fetched successfully");
    }

    //To fetch all other product variants of the same product,
    // excluding the one specified by productVariantId.
    public ResponseEntity<ApiResponse<Map<String,Object>>> getOtherVariantsByProductVariantId(Long productVariantId){
       ProductVariant productVariant=productVariantRepository.findById(productVariantId).orElseThrow(()-> new ProductVariantNotFound("product variant not found with this product variant id "+productVariantId));
       List<ProductVariant> productVariants=productVariantRepository.findByProductIdAndIdNot(productVariant.getProduct().getId(),productVariantId);
       List<ProductVariantDto> productVariantDtos=productVariantMapper.toDtoList(productVariants);
       return responseBuilder.build("similar product variants",productVariantDtos,"Similar product Variants fetched successfully");
    }

    //To fetch details of a specific product variant
    // and also save it in the recently viewed list of the current user.
    public ResponseEntity<ApiResponse<Map<String,Object>>> getProductVariant(Long productVariantId){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFound("User not found with this email "+email));

        ProductVariant productVariant=productVariantRepository.findById(productVariantId).orElseThrow(()->new ProductVariantNotFound("Product variant not found with this is "+productVariantId));

        ProductVariantDto productVariantDto=productVariantMapper.toDto(productVariant);
        recentlyViewedService.addToRecentlyViewedVariants(user.getId(),productVariantDto);

        return responseBuilder.build("product variant",productVariantDto,"Product variant fetched successfully");

    }

    //To fetch the list of product variants the logged-in user recently viewed.
    public ResponseEntity<ApiResponse<Map<String,Object>>> getRecentlyViewedProductVariants(){
         String email=authUtil.getCurrentUserEmail();
         User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFound("User not found with this email "+email));
         List<ProductVariantDto> productVariantDtos=recentlyViewedService.getRecentlyViewedVariants(user.getId());
         return responseBuilder.build("product variants",productVariantDtos,"All recently viewed product variants");
    }
}
