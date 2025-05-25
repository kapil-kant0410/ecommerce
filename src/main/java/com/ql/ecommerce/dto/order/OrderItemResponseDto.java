package com.ql.ecommerce.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {
    private Long productVariantId;
    private String productName;
    private String ProductVariantName; // size, color, etc.
    private Long quantity;
    private Long price;
}
