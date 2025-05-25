package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.order.OrderItemResponseDto;
import com.ql.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemMapper {

    public OrderItemResponseDto toDto(OrderItem orderItem){
        return OrderItemResponseDto.builder()
                .productVariantId(orderItem.getProductVariant().getId())
                .productName(orderItem.getProductVariant().getProduct().getName())
                .ProductVariantName(orderItem.getProductVariant().getSku())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .build();
    }

    public List<OrderItemResponseDto> toDtoList(List<OrderItem> orderItems){
        return orderItems.stream().map((this::toDto)).toList();
    }

}
