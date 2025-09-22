package com.pwdk.grocereach.Auth.Presentation.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmEmailChangeRequest {
    @NotBlank(message = "Token is required")
    private String token;
}