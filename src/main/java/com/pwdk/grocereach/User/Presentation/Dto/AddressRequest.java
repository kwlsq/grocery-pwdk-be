package com.pwdk.grocereach.User.Presentation.Dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank(message = "Label is required")
    private String label;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Full address is required")
    private String fullAddress;

    @NotNull(message = "Province ID is required")
    private Integer provinceId;

    @NotNull(message = "City ID is required")
    private Integer cityId;
    private String postalCode;
    private boolean isPrimary;
}