package com.ql.ecommerce.dto.paymentMethod;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentMethodRequestDto {

    @NotBlank(message = "Type is required")
    private String type;  // e.g., CARD, BANK, UPI
    @NotBlank(message = "Provider is required")
    private String provider; // e.g., Visa, MasterCard, SBI
    @NotBlank(message = "Account number is required")
    private String accountNumber; // last 4 digits or masked
    @NotBlank(message = "Expiry date is required")
    private String expiryDate; // MM/YY (for cards)

}
