package com.pwdk.grocereach.location.presentations.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTOs to map the JSON response from RajaOngkir's API.
 */
public class RajaOngkirDTOs {

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiResponse<T> {
        private Meta meta;
        private T data;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private int code;
        private String status;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CityData {
        private int id;
        private String name;
    }
}

