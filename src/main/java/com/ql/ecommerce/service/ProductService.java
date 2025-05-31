package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import com.ql.ecommerce.dto.ProductVariant.filter.ProductVariantFilter;
import com.ql.ecommerce.dto.product.filter.ProductFilter;
import com.ql.ecommerce.dto.product.response.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ProductService {

     //page (default: 0)
     //page number starts from 0
     //page size (default: 5)
     //sort (e.g., "name,asc", "createdAt,desc")
     //category (optional) (supports) (id,slug)
     //brands (optional)
     //This method retrieves a paginated, sorted, and filtered list of products based on the criteria provided in a ProductFilter object.
     ResponseEntity<ApiResponse<Map<String,Object>>> createProduct(ProductDto productDto);

     //: Fetches up to 10 similar products that belong to the same category as the given product, excluding the product itself.
     ResponseEntity<ApiResponse<Map<String,Object>>> getAllProducts(ProductFilter productFilter);

     //: Fetches up to 10 similar products that belong to the same brand as the given product, excluding the product itself
     ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByCategory(Long productId);

     //: Fetches up to 10 similar products that belong to the same brand as the given product, excluding the product itself.
     ResponseEntity<ApiResponse<Map<String,Object>>> getSimilarProductsByBrand(Long productId);

     //: Returns product details for the given ID and stores it in the user's recently viewed list.
     ResponseEntity<ApiResponse<Map<String,Object>>> getProductById(Long productId);

     //Retrieves the list of products recently viewed by the current logged-in user.
     ResponseEntity<ApiResponse<Map<String,Object>>> getRecentlyViewedProducts();

     //Allows the product owner to update the product details.
     ResponseEntity<ApiResponse<Map<String,Object>>> updateProduct(Long productId,ProductDto productDto);

     //Allows the product owner to delete the product.
     ResponseEntity<ApiResponse<Map<String,Object>>> deleteProduct(Long productId);


         //     Product variant
        //     To filter, sort, and paginate product variants based on multiple criteria such as:
       //     Color(s)
      //     Size(s)
     //     Price range
    //     Minimum rating
   //     Sorting (by any field)
  //     Pagination (page number and size)
 //     Colors String
//     sizes  String

     ResponseEntity<ApiResponse<Map<String,Object>>> getProductVariants(ProductVariantFilter filter);

     //To fetch all other product variants of the same product,
     // excluding the one specified by productVariantId.
     ResponseEntity<ApiResponse<Map<String,Object>>> getOtherVariantsByProductVariantId(Long productVariantId);

     //To fetch details of a specific product variant
     // and also save it in the recently viewed list of the current user.
     ResponseEntity<ApiResponse<Map<String,Object>>> getProductVariant(Long productVariantId);

     //To fetch the list of product variants the logged-in user recently viewed.
     ResponseEntity<ApiResponse<Map<String,Object>>> getRecentlyViewedProductVariants();
}
