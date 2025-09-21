package com.pwdk.grocereach.shipping.presentations.controllers;

import com.pwdk.grocereach.shipping.applications.services.ShippingService;
import com.pwdk.grocereach.shipping.presentations.dto.CheckoutAddressResponse;
import com.pwdk.grocereach.shipping.presentations.dto.ShippingCalculationRequest;
import com.pwdk.grocereach.shipping.presentations.dto.ShippingOptionResponse;
import com.pwdk.grocereach.common.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final ShippingService shippingService;
    @GetMapping("/addresses")
    public ResponseEntity<?> getCheckoutAddresses(Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            CheckoutAddressResponse response = shippingService.getUserAddressesForCheckout(userId);
            return Response.successfulResponse("Successfully retrieved addresses", response);
        } catch (Exception e) {
            return Response.failedResponse("Failed to retrieve addresses: " + e.getMessage());
        }
    }

    @PostMapping("/shipping/calculate")
    public ResponseEntity<?> calculateShippingOptions(
            @Valid @RequestBody ShippingCalculationRequest request,
            Authentication authentication) {

        try {
            if (request.getStoreId() == null || request.getAddressId() == null || request.getTotalWeight() == null) {
                return Response.failedResponse("Missing required parameters: storeId, addressId, or totalWeight");
            }

            List<ShippingOptionResponse> options = shippingService.calculateShippingOptions(request);
            return Response.successfulResponse("Successfully calculated shipping options", options);
        } catch (Exception e) {
            return Response.failedResponse("Failed to calculate shipping options: " + e.getMessage());
        }
    }

    @GetMapping("/shipping/cost")
    public ResponseEntity<?> getShippingCost(
            @RequestParam UUID storeId,
            @RequestParam UUID addressId,
            @RequestParam Integer weight,
            @RequestParam String courier,
            @RequestParam String service,
            Authentication authentication) {

        try {
            ShippingOptionResponse cost = shippingService.getShippingCost(storeId, addressId, weight, courier, service);
            return Response.successfulResponse("Successfully retrieved shipping cost", cost);
        } catch (Exception e) {
            return Response.failedResponse("Failed to retrieve shipping cost: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        return Response.successfulResponse("Checkout controller is working!");
    }
}