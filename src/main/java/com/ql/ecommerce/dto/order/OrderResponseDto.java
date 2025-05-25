package com.ql.ecommerce.dto.order;

import com.ql.ecommerce.dto.address.response.AddressDto;
import com.ql.ecommerce.dto.paymentMethod.PaymentMethodResponseDto;
import com.ql.ecommerce.dto.user.response.UserDto;
import com.ql.ecommerce.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private Long totalAmount;

    private UserDto customer;
    private AddressDto shippingAddress;
    private PaymentMethodResponseDto paymentMethod;

    private List<OrderItemResponseDto> orderItems;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
