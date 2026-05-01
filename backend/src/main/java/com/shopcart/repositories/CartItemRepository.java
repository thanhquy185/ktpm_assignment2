package com.shopcart.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopcart.entities.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

    void deleteByCartId(UUID cartId);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Long sumQuantity(@Param("cartId") UUID cartId);

    @Query("SELECT SUM(ci.quantity * ci.price) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Long sumPrice(@Param("cartId") UUID cartId);
}
