package com.pwdk.grocereach.Auth.Application.Services; // Or your correct services package

import com.pwdk.grocereach.Auth.Domain.ValueOfObject.Token;
import org.springframework.security.core.Authentication;

public interface TokenGeneratorService {
    Token generateAccessToken(Authentication authentication);
    Token generateRefreshToken(Authentication authentication);
}