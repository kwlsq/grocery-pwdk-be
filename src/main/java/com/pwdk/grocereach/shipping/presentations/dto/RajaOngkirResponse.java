package com.pwdk.grocereach.shipping.presentations.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RajaOngkirResponse {
    private RajaOngkirData rajaongkir;
}

