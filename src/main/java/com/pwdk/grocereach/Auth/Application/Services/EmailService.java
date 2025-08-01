package com.pwdk.grocereach.Auth.Application.Services;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
}

