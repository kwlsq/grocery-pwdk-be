package com.pwdk.grocereach.User.Presentation.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateEmailRequest {
    @NotBlank(message = "New email is required")
    @Email(message = "Please provide a valid email address")
    private String newEmail;

    @NotBlank(message = "Current password is required for security")
    private String currentPassword;
}