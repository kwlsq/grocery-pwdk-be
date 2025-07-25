package com.pwdk.grocereach.cart.application.impl;

import com.pwdk.grocereach.cart.application.CartService;
import com.pwdk.grocereach.cart.domain.entities.CartItems;
import com.pwdk.grocereach.cart.domain.entities.Product;
import com.pwdk.grocereach.cart.domain.entities.User;
import com.pwdk.grocereach.cart.infrastructure.repository.CartRepository;
import com.pwdk.grocereach.cart.infrastructure.repository.ProductRepository;
import com.pwdk.grocereach.cart.infrastructure.repository.UserRepository;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemRequest;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
        System.out.println("aaaaaaaaa");
        System.out.println(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        System.out.println(user);
        return cartRepository.findAllByUserAndDeletedAtIsNull(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CartItemResponse addCartItem(UUID userId, CartItemRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(request.productId()).orElseThrow();
        CartItems cartItem = CartItems.builder()
                .user(user)
                .product(product)
                .quantity(request.quantity())
                .build();
        cartRepository.save(cartItem);
        return toResponse(cartItem);
    }

    @Override
    public CartItemResponse updateQuantity(UUID cartItemId, int quantity) {
        CartItems cartItem = cartRepository.findById(cartItemId).orElseThrow();
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
        return toResponse(cartItem);
    }

    @Override
    public void deleteItem(UUID cartItemId) {
        cartRepository.deleteById(cartItemId);
    }

    @Override
    public void deleteMultiple(List<UUID> cartItemIds) {
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
