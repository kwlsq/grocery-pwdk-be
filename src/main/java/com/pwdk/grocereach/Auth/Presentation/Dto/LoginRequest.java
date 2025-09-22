package com.pwdk.grocereach.Auth.Presentation.Dto; // Or your correct DTO package

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}