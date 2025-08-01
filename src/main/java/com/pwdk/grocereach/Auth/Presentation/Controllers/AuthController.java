package com.pwdk.grocereach.Auth.Presentation.Controllers;

import com.pwdk.grocereach.Auth.Application.Services.AuthService;
import com.pwdk.grocereach.Auth.Presentation.Dto.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) { // No longer need HttpServletResponse here
        try {
            LoginResponse loginResponse = authService.login(request);

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", loginResponse.getAccessToken().getValue())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(15 * 60) // 15 minutes
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken().getValue())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(30 * 24 * 60 * 60) // 30 days
                    .sameSite("Lax")
                    .build();


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
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            LoginResponse loginResponse = authService.refreshToken(request);

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", loginResponse.getAccessToken().getValue())
                    .httpOnly(true).secure(false).path("/").maxAge(15 * 60).sameSite("Lax").build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken().getValue())
                    .httpOnly(true).secure(false).path("/").maxAge(30 * 24 * 60 * 60).sameSite("Lax").build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body("Token refreshed successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) RefreshTokenRequest request) { // Make body optional
        // Invalidate the token on the backend
        if (request != null && request.getRefreshToken() != null) {
            authService.logout(request);
        }

        // Create expired cookies to clear them from the browser
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Lax").build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Lax").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body("User logged out successfully.");
    }
}