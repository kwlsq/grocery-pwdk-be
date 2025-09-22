package com.pwdk.grocereach.Auth.Application.Services;

public interface VerificationTokenService {
    void storeToken(String token);
    boolean isTokenUsedOrExpired(String token);
    void invalidateToken(String token);
}
