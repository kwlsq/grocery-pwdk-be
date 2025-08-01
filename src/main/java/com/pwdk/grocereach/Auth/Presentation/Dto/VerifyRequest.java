package com.pwdk.grocereach.Auth.Presentation.Dto;

import lombok.Data;

@Data
public class VerifyRequest {
    private String token;
    private String password;
}

