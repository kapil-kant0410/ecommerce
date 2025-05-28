package com.ql.ecommerce.dto.paymentMethod;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePaymentMethodRequestDto {
    @NotBlank(message = "Expiry date is required")
    private String expiryDate; // MM/YY (for cards)
}
