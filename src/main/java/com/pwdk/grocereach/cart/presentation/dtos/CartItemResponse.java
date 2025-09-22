package com.pwdk.grocereach.cart.presentation.dtos;
import java.util.UUID;

public record CartItemResponse(
        UUID id,
        UUID productId,
        String productName,
        int quantity,
        double price
) {}