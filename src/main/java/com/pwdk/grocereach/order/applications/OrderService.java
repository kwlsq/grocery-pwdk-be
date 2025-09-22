package com.pwdk.grocereach.order.applications;

import com.pwdk.grocereach.order.presentations.dtos.CreateOrderRequest;
import com.pwdk.grocereach.order.presentations.dtos.InvoiceResponse;

public interface OrderService {
    InvoiceResponse createOrder(String userId, CreateOrderRequest request);
}


