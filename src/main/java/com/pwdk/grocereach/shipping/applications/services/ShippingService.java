package com.pwdk.grocereach.shipping.applications.services;

import com.pwdk.grocereach.shipping.presentations.dto.CheckoutAddressResponse;
import com.pwdk.grocereach.shipping.presentations.dto.ShippingCalculationRequest;
import com.pwdk.grocereach.shipping.presentations.dto.ShippingOptionResponse;

import java.util.List;
import java.util.UUID;

public interface ShippingService {

    CheckoutAddressResponse getUserAddressesForCheckout(UUID userId);
    List<ShippingOptionResponse> calculateShippingOptions(ShippingCalculationRequest request);
    ShippingOptionResponse getShippingCost(UUID storeId, UUID addressId, Integer weight,
                                           String courier, String service);
}