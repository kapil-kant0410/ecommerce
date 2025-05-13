package com.ql.ecommerce.dto.product.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name should not exceed 255 characters")
    private String name;

    @NotBlank(message = "Short description is required")
    @Size(max = 500, message = "Short description should not exceed 500 characters")
    private String shortDescription;

    @NotBlank(message = "full description is required")
    private String fullDescription;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

}
