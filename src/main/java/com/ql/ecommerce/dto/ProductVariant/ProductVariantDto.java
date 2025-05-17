package com.ql.ecommerce.dto.ProductVariant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDto {

    @NotBlank(message = "Product variant sku is required")
    private String sku;

    @NotBlank(message = "Product variant size is required")
    private String size;

    @NotBlank(message = "Product variant color is required")
    private String color;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be at least 1")
    private Long price;

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be at least 0")
    private Integer rating;

    @NotNull(message = "stockQuantity is required")
    @Min(value = 1, message = "stockQuantity must be at least 1")
    private Long stockQuantity;

    @NotNull(message = "reservedQuantity is required")
    @Min(value = 0, message = "reservedQuantity must be at least 0")
    private Long reservedQuantity;

    @NotNull(message = "productId is required")
    @Min(value = 1, message = "productId must be at least 1")
    private Long productId;

}
