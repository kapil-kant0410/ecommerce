package com.ql.ecommerce.repository;

import com.ql.ecommerce.entity.Cart;
import com.ql.ecommerce.entity.CartItem;
import com.ql.ecommerce.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
       List<CartItem> findByCart(Cart cart);
       Optional<CartItem> findByCartAndProductVariant(Cart cart, ProductVariant productVariant);
       Optional<CartItem> findByCartIdAndProductVariant(Long cartId, ProductVariant productVariant);
}
