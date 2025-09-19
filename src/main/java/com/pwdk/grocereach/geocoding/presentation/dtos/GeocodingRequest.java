package com.pwdk.grocereach.geocoding.presentation.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeocodingRequest {

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 10, message = "Language code must not exceed 10 characters")
    @Builder.Default
    private String language = "id";  // Default to Indonesian

    @Size(max = 10, message = "Region code must not exceed 10 characters")
    @Builder.Default
    private String region = "id";    // Default to Indonesia

    @JsonProperty("country_code")
    @Size(max = 2, message = "Country code must be 2 characters")
    private String countryCode;

    // Constructor for convenience - address only
    public GeocodingRequest(String address) {
        this.address = address;
        this.language = "id";
        this.region = "id";
    }

    // Constructor with custom language and region
    public GeocodingRequest(String address, String language, String region) {
        this.address = address;
        this.language = language != null ? language : "id";
        this.region = region != null ? region : "id";
    }
}