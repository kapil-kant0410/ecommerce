package com.ql.ecommerce.dto.ProductVariant.filter;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ProductVariantFilter {

    private int page = 0;                     // default page
    private int size = 10;                    // default size
    private String direction = "asc";         // sort direction
    private String sortBy = "id";             // sort field
    private int rating;                       // optional minimum rating
    private List<String> colors;              // optional
    private List<String> sizes;               // optional
    private Long minPrice;                    // optional
    private Long maxPrice;                    // optional

}