package com.pwdk.grocereach.Auth.Presentation.Controllers;

import com.pwdk.grocereach.Auth.Application.Services.AuthService;
import com.pwdk.grocereach.Auth.Infrastructure.Securities.CookieUtil;
import com.pwdk.grocereach.Auth.Presentation.Dto.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok("Registration successful. Please check your email to verify your account.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest request) {
        try {
            authService.verifyAccount(request);
            return ResponseEntity.ok("Account verified successfully. You can now log in.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = authService.login(request);

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", loginResponse.getAccessToken().getValue())
                    .httpOnly(true).secure(false).path("/").maxAge(15 * 60).sameSite("Lax").build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken().getValue())
                    .httpOnly(true).secure(false).path("/api/v1/auth").maxAge(30 * 24 * 60 * 60).sameSite("Lax").build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body("Login successful.");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = CookieUtil.getCookieValue(request, "refreshToken")
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found in cookie."));

        try {
            LoginResponse loginResponse = authService.refreshToken(refreshToken);

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", loginResponse.getAccessToken().getValue())
                    .httpOnly(true).secure(false).path("/").maxAge(15 * 60).sameSite("Lax").build();

            ResponseCookie newRefreshTokenCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken().getValue())
                    .httpOnly(true).secure(false).path("/api/v1/auth").maxAge(30 * 24 * 60 * 60).sameSite("Lax").build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString())
                    .body("Token refreshed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        String email = authentication.getName();
        authService.logout(email);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "").httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Lax").build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "").httpOnly(true).secure(false).path("/api/auth").maxAge(0).sameSite("Lax").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body("User logged out successfully.");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> payload) {
        try {
            authService.resendVerification(payload.get("email"));
            return ResponseEntity.ok("A new verification email has been sent. Please check your inbox.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        try {
            authService.requestPasswordReset(payload.get("email"));
            // Always return a generic success message to prevent email enumeration attacks
            return ResponseEntity.ok("If an account with that email exists, a password reset link has been sent.");
        } catch (Exception e) {
            // Even if an error occurs (like user not found), send a generic success message.
            return ResponseEntity.ok("If an account with that email exists, a password reset link has been sent.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        try {
            String token = payload.get("token");
            String newPassword = payload.get("newPassword");
            authService.confirmPasswordReset(token, newPassword);
            return ResponseEntity.ok("Your password has been successfully reset. You can now log in.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/store-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerStoreAdmin(@RequestBody RegisterRequest request) {
        try {
            authService.registerStoreAdmin(request);
            return ResponseEntity.ok("Registration successful. Please check your email to verify your account.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
