package com.pwdk.grocereach.cart.application.impl;

import com.pwdk.grocereach.cart.application.CartService;
import com.pwdk.grocereach.cart.domain.entities.CartItems;
import com.pwdk.grocereach.cart.infrastructure.repository.CartRepository;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemRequest;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository){
        this.cartRepository = cartRepository;
    }

    @Override
    public List<CartItemResponse> getCartItems(UUID userId) {
        return cartRepository.findAllByUserIdAndDeletedAtIsNull(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

//    private CartItemResponse toResponse(CartItems item) {
//        return new CartItemResponse(
//                item.getId(),
//                item.getProduct().getId(),
//                item.getProduct().getName(),
//                item.getQuantity(),
//                item.getProduct().getCurrentVersion() != null
//                        ? item.getProduct().getCurrentVersion().getPrice().doubleValue()
//                        : 0.0
//        );
//    }
}
