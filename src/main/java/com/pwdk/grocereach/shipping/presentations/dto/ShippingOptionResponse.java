package com.pwdk.grocereach.shipping.presentations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingOptionResponse {
    private String courier;
    private String service;
    private String serviceName;
    private Long cost;
    private String etd;
    private String description;
}