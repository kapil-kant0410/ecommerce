package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.cart.AddToCartRequestDto;
import com.ql.ecommerce.dto.cart.CartItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CartService {
    ResponseEntity<ApiResponse<Map<String,Object>>> getCurrentUserCart();
    ResponseEntity<ApiResponse<Map<String,Object>>> addItemToCart(AddToCartRequestDto addToCartRequestDto);
    ResponseEntity<ApiResponse<Map<String,Object>>> updateCartItemQuantity (Long cartId,AddToCartRequestDto addToCartRequestDto);
    ResponseEntity<ApiResponse<Map<String,Object>>> removeCartItem(Long cartItemId);
    ResponseEntity<ApiResponse<Map<String,Object>>> clearCart();
    ResponseEntity<ApiResponse<Map<String,Object>>> getCartSummary();
}
