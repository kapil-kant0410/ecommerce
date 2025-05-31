package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.cart.AddToCartRequestDto;
import com.ql.ecommerce.dto.cart.CartItemDto;
import com.ql.ecommerce.dto.cart.CartSummaryDto;
import com.ql.ecommerce.entity.*;
import com.ql.ecommerce.exception.*;
import com.ql.ecommerce.mapper.CartItemMapper;
import com.ql.ecommerce.repository.CartItemRepository;
import com.ql.ecommerce.repository.CartRepository;
import com.ql.ecommerce.repository.ProductVariantRepository;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.CartService;
import com.ql.ecommerce.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class CartServiceImpl implements CartService {

    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final ResponseBuilder responseBuilder;
    private final Logger logger= LoggerFactory.getLogger(CartServiceImpl.class);

    public CartServiceImpl(ResponseBuilder responseBuilder,CartItemMapper cartItemMapper,CartItemRepository cartItemRepository,CartRepository cartRepository,ProductVariantRepository productVariantRepository,UserRepository userRepository, AuthUtil authUtil){
        this.authUtil=authUtil;
        this.userRepository=userRepository;
        this.productVariantRepository=productVariantRepository;
        this.cartRepository=cartRepository;
        this.cartItemRepository=cartItemRepository;
        this.cartItemMapper=cartItemMapper;
        this.responseBuilder=responseBuilder;
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUserCart(){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with email "+email));
        Cart cart=cartRepository.findByUser(user)
                .orElseGet(()->{
                    Cart newCart=new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        List<CartItem> cartItems=cartItemRepository.findByCart(cart);
        List<CartItemDto> cartItemDtos=cartItemMapper.toDtoList(cartItems);

        return responseBuilder.build("Cart items",cartItemDtos,"Cart fetched successfully");
    }

    public ResponseEntity<ApiResponse<Map<String,Object>>> addItemToCart(AddToCartRequestDto addToCartRequestDto){

        String email = authUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with email: " + email));

        ProductVariant productVariant = productVariantRepository.findById(addToCartRequestDto.getProductVariantId())
                .orElseThrow(() -> new ProductVariantNotFound("ProductVariant not found with ID: " + addToCartRequestDto.getProductVariantId()));

        long availableQty = productVariant.getStockQuantity() - productVariant.getReservedQuantity();

        if(availableQty<=0){
            throw new BadRequest("Product is out of stock");
        }

        if (addToCartRequestDto.getQuantity() > availableQty) {
            throw new BadRequest("Only " + availableQty + " items available in stock");
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(()->{
                    Cart newCart=new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItemOpt=cartItemRepository.findByCartAndProductVariant(cart,productVariant);
        CartItem savedItem;

        if(existingItemOpt.isPresent()){
            CartItem existingItem = existingItemOpt.get();
            long newQty = existingItem.getQuantity() + addToCartRequestDto.getQuantity();
            if (addToCartRequestDto.getQuantity() > (productVariant.getStockQuantity() - productVariant.getReservedQuantity())) {
                throw new BadRequest("Total quantity exceeds available stock");
            }
            existingItem.setQuantity(newQty);
            savedItem= cartItemRepository.save(existingItem);
        }else{
            CartItem cartItem=new CartItem();
            cartItem.setCart(cart);
            cartItem.setQuantity(addToCartRequestDto.getQuantity());
            cartItem.setProductVariant(productVariant);
            cartItem.setPrice(productVariant.getPrice());
            savedItem= cartItemRepository.save(cartItem);
        }

        CartItemDto cartItemDto=cartItemMapper.toDto(savedItem);

        return responseBuilder.build("Cart item",cartItemDto,"Item added to cart successfully");
    }

    public ResponseEntity<ApiResponse<Map<String,Object>>> updateCartItemQuantity (Long cartId,AddToCartRequestDto addToCartRequestDto){

          String email=authUtil.getCurrentUserEmail();

          User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email "+ email));
          ProductVariant productVariant=productVariantRepository.findById(addToCartRequestDto.getProductVariantId()).orElseThrow(()-> new ProductVariantNotFound("product variant not found for this product variant id "+addToCartRequestDto.getProductVariantId()));
          CartItem cartItem=cartItemRepository.findByCartIdAndProductVariant(cartId,productVariant).orElseThrow(()-> new CartItemNotFound("cart item not found for this cart id and product variant id"));

          if(!cartItem.getCart().getUser().getId().equals(user.getId())){
              throw new  Forbidden("User not allowed to update this cart");
          }

          if (addToCartRequestDto.getQuantity() > productVariant.getStockQuantity()) {
            throw new BadRequest("Requested quantity exceeds available stock");
          }

        Long existingQty = cartItem.getQuantity();
        Long newQty = addToCartRequestDto.getQuantity();

        long availableQty = (productVariant.getStockQuantity() - productVariant.getReservedQuantity()) + existingQty;

        if (newQty > availableQty) {
            throw new BadRequest("Requested quantity exceeds available stock. Max allowed: " + availableQty);
        }

          cartItem.setQuantity(addToCartRequestDto.getQuantity());
          cartItemRepository.save(cartItem);

        CartItemDto cartItemDto=cartItemMapper.toDto(cartItem);

        return responseBuilder.build("Cart item",cartItemDto,"cart item quantity updated successfully");
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> removeCartItem(Long cartItemId){

        String email = authUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with this email " + email));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFound("Cart item not found for ID: " + cartItemId));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new Forbidden("User not allowed to delete this cart item");
        }

        cartItemRepository.delete(cartItem);

        CartItemDto cartItemDto=cartItemMapper.toDto(cartItem);

        return responseBuilder.build("Deleted cart item",cartItemDto,"Cart item removed successfully");
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> clearCart(){

        String email=authUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with this email " + email));

        Cart cart=user.getCart();

        if (cart == null) {
           throw new CartNotFound("Cart not found for user: " + email);
        }

        List<CartItem> cartItems=cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
           throw new BadRequest("Cart is already empty");
        }

        List<CartItemDto> cartItemDtos=cartItemMapper.toDtoList(cartItems);

        cartItemRepository.deleteAll(cartItems);

        return responseBuilder.build("Deleted cart items",cartItemDtos,"All cart items cleared successfully");
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> getCartSummary(){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email"+email));

        Cart cart= user.getCart();

        if(cart==null){
            throw new CartNotFound("Cart not found for user: " + email);
        }

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        double subtotal = cartItems.stream()
                .mapToDouble(item -> item.getProductVariant().getPrice() * item.getQuantity())
                .sum();

        double discount = subtotal > 1000 ? 200.0 : 0.0; // Example: ₹200 off for subtotal > ₹1000
        double tax = subtotal * 0.09;                   // Example: 9% GST
        double total = subtotal - discount + tax;

        CartSummaryDto summary = CartSummaryDto.builder()
                .subtotal(subtotal)
                .discount(discount)
                .tax(tax)
                .total(total)
                .build();

        return responseBuilder.build("Summary",summary,"Cart summary fetched successfully");
    }

}
