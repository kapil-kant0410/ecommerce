package com.ql.ecommerce.dto.paymentMethod;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentMethodResponseDto {
    private String type;
    private String provider;
    private String accountNumber;
    private String expiryDate;
    private boolean isDefault;
}

