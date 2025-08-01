package com.pwdk.grocereach.Auth.Presentation.Dto; // Or your correct DTO package

import com.pwdk.grocereach.Auth.Domain.ValueOfObject.Token;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Token accessToken;
    private Token refreshToken;
}