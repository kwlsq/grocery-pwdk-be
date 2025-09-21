package com.pwdk.grocereach.shipping.presentations.dto.rajaongkir;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KomerceShippingData {
    private String name;
    private String code;
    private String service;
    private String description;
    private Long cost;
    private String etd;
}
