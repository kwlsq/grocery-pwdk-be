package com.pwdk.grocereach.User.Presentation.Dto; // Or your correct DTO package

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Full name cannot be empty")
    private String fullName;

}