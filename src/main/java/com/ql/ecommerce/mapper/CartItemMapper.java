package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.cart.CartItemDto;
import com.ql.ecommerce.entity.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartItemMapper {

   public CartItemDto toDto(CartItem cartItem){
        return CartItemDto.builder()
                .productVariantId(cartItem.getProductVariant().getId())
                .quantity(cartItem.getQuantity())
                .build();
   }

   public List<CartItemDto> toDtoList(List<CartItem> cartItems){
        return cartItems.stream().map(this::toDto).toList();
   }

}
