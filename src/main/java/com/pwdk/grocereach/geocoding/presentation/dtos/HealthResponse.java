package com.pwdk.grocereach.geocoding.presentation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public  class HealthResponse {
    private String message;
    private String status;
}
