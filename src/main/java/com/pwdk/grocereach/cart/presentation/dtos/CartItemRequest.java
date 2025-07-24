package com.pwdk.grocereach.cart.presentation.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CartItemRequest(
        @NotNull UUID productId,
        @Min(1) int quantity
) {}