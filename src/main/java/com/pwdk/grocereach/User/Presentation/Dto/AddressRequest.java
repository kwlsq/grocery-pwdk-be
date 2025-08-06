package com.pwdk.grocereach.User.Presentation.Dto;
import jakarta.validation.constraints.NotBlank;
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

    private String city;
    private String province;
    private String postalCode;
    private boolean isPrimary;
}