package com.pwdk.grocereach.cart.application;

import com.pwdk.grocereach.cart.presentation.dtos.CartItemRequest;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemResponse;

import java.util.List;
import java.util.UUID;

public interface CartService {
    List<CartItemResponse> getCartItems(UUID userId);
    CartItemResponse addCartItem(UUID userId, CartItemRequest request);
    CartItemResponse updateQuantity(UUID cartItemId, int quantity);
    void deleteItem(UUID cartItemId);
    void deleteMultiple(List<UUID> cartItemIds);
}
