package com.pwdk.grocereach.shipping.presentations.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ShippingCalculationRequest {
    private UUID storeId;
    private UUID addressId;
    private Integer totalWeight;
}