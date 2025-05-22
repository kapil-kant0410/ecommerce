package com.ql.ecommerce.dto.cart;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartSummaryDto {
    private double subtotal;
    private double discount;
    private double tax;
    private double total;
}

