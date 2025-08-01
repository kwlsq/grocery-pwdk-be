package com.pwdk.grocereach.cart.presentation;

import com.pwdk.grocereach.cart.application.CartService;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemRequest;
import com.pwdk.grocereach.cart.presentation.dtos.CartItemResponse;
import com.pwdk.grocereach.common.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart-items")
public class CartRestController {
    private final CartService cartService;

    public CartRestController(CartService cartService){
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Response<List<CartItemResponse>>> getMyCartItems(@RequestParam UUID userId) {
        List<CartItemResponse> items = cartService.getCartItems(userId);
        return Response.successfulResponse("Cart items fetched successfully", items);
    }

    @PostMapping
    public ResponseEntity<Response<CartItemResponse>> addItemToCart(
            @Valid @RequestBody CartItemRequest request,
            @RequestParam UUID userId
    ) {
        CartItemResponse item = cartService.addCartItem(userId, request);
        return Response.successfulResponse("Item added to cart successfully", item);
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<Response<CartItemResponse>> updateCartItemQuantity(
            @PathVariable UUID cartItemId,
            @RequestBody Map<String, Integer> payload
    ) {
        Integer quantity = payload.get("quantity");
        CartItemResponse updatedItem = cartService.updateQuantity(cartItemId, quantity);
        return Response.successfulResponse("Cart item quantity updated successfully", updatedItem);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Response<Void>> deleteCartItem(@PathVariable UUID cartItemId) {
        cartService.deleteItem(cartItemId);
        return Response.successfulResponse("Cart item deleted successfully");
    }

    @DeleteMapping
    public ResponseEntity<Response<Void>> deleteMultipleCartItems(
            @RequestBody Map<String, List<UUID>> body
    ) {
        List<UUID> cartItemIds = body.get("cartItemIds");
        cartService.deleteMultiple(cartItemIds);
        return Response.successfulResponse("Multiple cart items deleted successfully");
    }
}
