package com.ql.ecommerce.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequestDto {
    @NotNull(message = "Product variant ID is required")
    private Long productVariantId;
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;
}
