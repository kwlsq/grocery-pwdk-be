package com.pwdk.grocereach.cart.application.impl;

import com.pwdk.grocereach.cart.application.CartService;
import com.pwdk.grocereach.cart.domain.entities.CartItems;
import com.pwdk.grocereach.product.domains.entities.Product;
import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.cart.infrastructure.repository.CartRepository;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductRepository;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemRequest;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository){
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<CartItemResponse> getCartItems(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return cartRepository.findAllByUserAndDeletedAtIsNull(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CartItemResponse addCartItem(UUID userId, CartItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.productId()));
        
        // Try to find existing cart item for this user and product
        CartItems existingCartItem = cartRepository.findByUserAndProductAndDeletedAtIsNull(user, product);
        
        // Try alternative method using product ID if the first method didn't work
        if (existingCartItem == null) {
            existingCartItem = cartRepository.findByUserAndProduct_IdAndDeletedAtIsNull(user, request.productId());
        }
        
        if (existingCartItem != null) {
            // Update the quantity of the existing cart item
            int newQuantity = existingCartItem.getQuantity() + request.quantity();
            existingCartItem.setQuantity(newQuantity);
            CartItems savedItem = cartRepository.save(existingCartItem);
            return toResponse(savedItem);
        }
        
        // Create a new cart item if the product doesn't exist in the cart
        CartItems cartItem = CartItems.builder()
                .user(user)
                .product(product)
                .quantity(request.quantity())
                .build();
        CartItems savedItem = cartRepository.save(cartItem);
        return toResponse(savedItem);
    }

    @Override
    public CartItemResponse updateQuantity(UUID cartItemId, int quantity) {
        CartItems cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId));
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
        return toResponse(cartItem);
    }

    @Override
    public void deleteItem(UUID cartItemId) {
        if (!cartRepository.existsById(cartItemId)) {
            throw new RuntimeException("Cart item not found with ID: " + cartItemId);
        }
        cartRepository.deleteById(cartItemId);
    }

    @Override
    public void deleteMultiple(List<UUID> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new RuntimeException("Cart item IDs list cannot be null or empty");
        }
        cartRepository.deleteAllByIdIn(cartItemIds);
    }

    private CartItemResponse toResponse(CartItems item) {
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getProduct().getCurrentVersion().getPrice().doubleValue()
        );
    }
}
