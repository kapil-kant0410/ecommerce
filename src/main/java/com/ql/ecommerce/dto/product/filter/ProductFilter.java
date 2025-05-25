package com.ql.ecommerce.dto.product.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ProductFilter {
    private int page = 0;                     // default page
    private int size = 10;                    // default size
    private String direction = "asc";         // sort direction
    private String sortBy = "id";             // sort field
    private String category;                  // optional
    private List<String> brands;              // optional
}
