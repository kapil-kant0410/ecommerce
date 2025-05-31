package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.ProductVariant.filter.ProductVariantFilter;
import com.ql.ecommerce.dto.product.filter.ProductFilter;
import com.ql.ecommerce.dto.product.response.ProductDto;
import com.ql.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    Logger logger= LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Map<String, Object>>> createProduct(@Valid @RequestBody ProductDto productDto){
          return productService.createProduct(productDto);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAllProducts(@ModelAttribute ProductFilter productFilter){
        return productService.getAllProducts(productFilter);
    }

    @GetMapping("/{productId}/similar-by-category")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByCategory(@PathVariable Long productId) {
        return productService.getSimilarProductsByCategory(productId);
    }

    @GetMapping("/{productId}/similar-by-brand")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByBrand(@PathVariable Long productId) {
        return productService.getSimilarProductsByBrand(productId);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getProductById(@PathVariable Long productId){
        return productService.getProductById(productId);
    }

    @GetMapping("/recently-viewed")
    ResponseEntity<ApiResponse<Map<String,Object>>> getRecentlyViewedProducts(){
        return productService.getRecentlyViewedProducts();
    }

    @PutMapping("/{productId}")
    ResponseEntity<ApiResponse<Map<String,Object>>> updateProduct(@PathVariable Long productId,@RequestBody @Valid ProductDto productDto){
        return productService.updateProduct(productId,productDto);
    }

    @DeleteMapping("/{productId}")
    ResponseEntity<ApiResponse<Map<String,Object>>> deleteProduct(@PathVariable Long productId){
        return productService.deleteProduct(productId);
    }

             //product variants apis
            //Product variant
           // To filter, sort, and paginate product variants based on multiple criteria such as:
          // Color(s)
        // Size(s)
       // Price range
      // Minimum rating
     // Sorting (by any field)
    //  Pagination (page number and size)

    @GetMapping("/product-variant/all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductVariants(@ModelAttribute ProductVariantFilter filter) {
        return productService.getProductVariants(filter);
    }

    //To fetch all other product variants of the same product,
    // excluding the one specified by productVariantId.

    @GetMapping("/product-variant/{productVariantId}/other-variants")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getOtherVariantsByProductVariantId(@PathVariable Long productVariantId) {
        return productService.getOtherVariantsByProductVariantId(productVariantId);
    }

    //To fetch details of a specific product variant
    // and also save it in the recently viewed list of the current user.

    @GetMapping("/product-variant/{productVariantId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductVariant(@PathVariable Long productVariantId){
           return productService.getProductVariant(productVariantId);
    }

    //To fetch the list of product variants the logged-in user recently viewed.

    @GetMapping("/product-variant/recently-viewed")
    ResponseEntity<ApiResponse<Map<String,Object>>> getRecentlyViewedProductVariants(){
          return productService.getRecentlyViewedProductVariants();
    }

}
