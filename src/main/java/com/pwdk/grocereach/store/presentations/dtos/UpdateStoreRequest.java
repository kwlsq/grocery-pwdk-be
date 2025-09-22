package com.pwdk.grocereach.store.presentations.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateStoreRequest {
    @NotBlank(message = "Store name cannot be blank")
    private String name;

    private String description;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Active status is required")
    private Boolean isActive; // No default for updates - must be explicitly set
}