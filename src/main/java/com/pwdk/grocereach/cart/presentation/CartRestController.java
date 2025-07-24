package com.pwdk.grocereach.cart.presentation;

import com.pwdk.grocereach.cart.application.CartService;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemRequest;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cart-items")
public class CartRestController {
    private final CartService cartService;

    public CartRestController(CartService cartService){
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getMyCartItems(Authentication auth) {
        UUID userId = getUserId(auth);
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addItemToCart(
            @Valid @RequestBody CartItemRequest request,
            Authentication auth
    ) {
        UUID userId = getUserId(auth);
        return ResponseEntity.ok(cartService.addCartItem(userId, request));
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(
            @PathVariable UUID cartItemId,
            @RequestBody Map<String, Integer> payload
    ) {
        Integer quantity = payload.get("quantity");
        return ResponseEntity.ok(cartService.updateQuantity(cartItemId, quantity));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable UUID cartItemId) {
        cartService.deleteItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMultipleCartItems(
            @RequestBody Map<String, List<UUID>> body
    ) {
        List<UUID> cartItemIds = body.get("cartItemIds");
        cartService.deleteMultiple(cartItemIds);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserId(Authentication auth) {
        return UUID.fromString(auth.getName());
    }
}
