package com.pwdk.grocereach.order.presentations;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pwdk.grocereach.common.Response;
import com.pwdk.grocereach.order.applications.OrderService;
import com.pwdk.grocereach.order.presentations.dtos.CreateOrderRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<?> createOrder(Authentication authentication, @RequestBody CreateOrderRequest request) {
        try {
            String userId = authentication.getName();
            return Response.successfulResponse("Order created successfully", orderService.createOrder(userId, request));
        } catch (Exception e) {
            return Response.failedResponse("Failed to create order: " + e.getMessage());
        }
    }
}


