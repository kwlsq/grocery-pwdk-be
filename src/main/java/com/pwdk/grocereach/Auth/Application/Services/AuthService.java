package com.pwdk.grocereach.Auth.Application.Services;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Presentation.Dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService {
    LoginResponse register(RegisterRequest request);
    void verifyAccount(VerifyRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(String refreshToken);
    void logout(String userId);
    void resendVerification(String email);
    void requestPasswordReset(String email);
    void confirmPasswordReset(String token, String newPassword);
    UserResponse registerStoreAdmin(RegisterRequest request);
    void confirmEmailChange(String token);

}

