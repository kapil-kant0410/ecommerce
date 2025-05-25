package com.ql.ecommerce.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequestDto {
    @NotNull(message = "Address ID is required")
    private Long addressId;

    @NotNull(message = "Payment Method ID is required")
    private Long paymentMethodId;

    @Size(max = 50, message = "Coupon code can't exceed 50 characters")
    private String couponCode;

    @Size(max = 255, message = "Delivery instructions can't exceed 255 characters")
    private String deliveryInstructions;
}
