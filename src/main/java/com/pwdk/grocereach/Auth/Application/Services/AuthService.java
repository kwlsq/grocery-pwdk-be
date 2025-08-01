package com.pwdk.grocereach.Auth.Application.Services;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Presentation.Dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService {
    User register(RegisterRequest request);
    void verifyAccount(VerifyRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);

}

