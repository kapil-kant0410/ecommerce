package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.cart.AddToCartRequestDto;
import com.ql.ecommerce.dto.cart.CartItemDto;
import com.ql.ecommerce.entity.CartItem;
import com.ql.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService){
        this.cartService=cartService;
    }

    @GetMapping()
    ResponseEntity<ApiResponse<Map<String, List<CartItemDto>>>> getCurrentUserCart(){
          return cartService.getCurrentUserCart();
    }

    @PostMapping()
    ResponseEntity<ApiResponse<Map<String,CartItemDto>>> addItemToCart(@Valid @RequestBody AddToCartRequestDto addToCartRequestDto){
         return cartService.addItemToCart(addToCartRequestDto);
    }

    @PutMapping("/{cartId}")
    ResponseEntity<ApiResponse<Map<String,CartItemDto>>> updateCartItemQuantity (@PathVariable Long cartId,@Valid @RequestBody AddToCartRequestDto addToCartRequestDto){
        return cartService.updateCartItemQuantity(cartId,addToCartRequestDto);
    }

    @DeleteMapping("/{cartItemId}")
    ResponseEntity<ApiResponse<Map<String, Object>>> removeCartItem(@PathVariable Long cartItemId){
        return cartService.removeCartItem(cartItemId);
    }

    @DeleteMapping()
    ResponseEntity<ApiResponse<Map<String, Object>>> clearCart(){
        return cartService.clearCart();
    }

    @GetMapping("/summary")
    ResponseEntity<ApiResponse<Map<String, Object>>> getCartSummary(){
        return cartService.getCartSummary();
    }

}
