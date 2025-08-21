package com.pwdk.grocereach.Auth.Application.Implements;

import com.pwdk.grocereach.Auth.Application.Services.AuthService;
import com.pwdk.grocereach.Auth.Application.Services.EmailService;
import com.pwdk.grocereach.Auth.Application.Services.TokenGeneratorService;
import com.pwdk.grocereach.Auth.Application.Services.UserService;
import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;
import com.pwdk.grocereach.Auth.Domain.ValueOfObject.Token;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.Auth.Infrastructure.Securities.CustomUserDetails;
import com.pwdk.grocereach.Auth.Presentation.Dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;
    private final AuthenticationManager authenticationManager;
    private final TokenGeneratorService tokenGeneratorService;
    private final JwtDecoder refreshTokenDecoder;
    private final UserService userService;
    @Qualifier("refreshTokenDecoder")

    @Override
    public User register(RegisterRequest request) {
        // This method is correct and unchanged
        userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
            if (existingUser.isVerified()) {
                throw new IllegalStateException("Email is already in use.");
            }
            throw new IllegalStateException("This email has been registered but not verified.");
        });

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setFullName(request.getFullName());
        newUser.setVerified(false);
        String randomPassword = UUID.randomUUID().toString();
        newUser.setPassword(passwordEncoder.encode(randomPassword));
        User savedUser = userRepository.save(newUser);

        String token = UUID.randomUUID().toString();
        String redisKey = "verification_token:" + token;
        redisTemplate.opsForValue().set(redisKey, savedUser.getId().toString(), 1, TimeUnit.HOURS);

        emailService.sendVerificationEmail(savedUser.getEmail(), token);

        return savedUser;
    }

    @Override
    public void verifyAccount(VerifyRequest request) {
        // This method is correct and unchanged
        String redisKey = "verification_token:" + request.getToken();
        String userId = redisTemplate.opsForValue().get(redisKey);

        if (userId == null) {
            throw new IllegalStateException("Verification token is invalid or has expired.");
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalStateException("User associated with token not found."));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(true);
        userRepository.save(user);

        redisTemplate.delete(redisKey);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Token accessToken = tokenGeneratorService.generateAccessToken(authentication);
        Token refreshToken = tokenGeneratorService.generateRefreshToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getUser().getId().toString();
        String redisKey = "refresh_token:" + userId;

        redisTemplate.opsForValue().set(redisKey, refreshToken.getValue(), 30, TimeUnit.DAYS);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        Jwt decodedRefreshToken = refreshTokenDecoder.decode(refreshToken);
        String userId = decodedRefreshToken.getSubject();

        String redisKey = "refresh_token:" + userId;

        String tokenFromRedis = redisTemplate.opsForValue().get(redisKey);
        if (tokenFromRedis == null || !tokenFromRedis.equals(refreshToken)) {
            throw new IllegalStateException("Invalid refresh token.");
        }

        UserDetails userDetails = userService.loadUserById(UUID.fromString(userId)); // Use loadUserById
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        Token newAccessToken = tokenGeneratorService.generateAccessToken(authentication);
        Token newRefreshToken = tokenGeneratorService.generateRefreshToken(authentication);

        redisTemplate.opsForValue().set(redisKey, newRefreshToken.getValue(), 30, TimeUnit.DAYS);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String userId) {

        String redisKey = "refresh_token:" + userId;
        redisTemplate.delete(redisKey);
    }

    @Override
    public void resendVerification(String email) {
        // This method is correct and unchanged
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with this email does not exist."));

        if (user.isVerified()) {
            throw new IllegalStateException("This account has already been verified.");
        }

        String token = UUID.randomUUID().toString();
        String redisKey = "verification_token:" + token;
        redisTemplate.opsForValue().set(redisKey, user.getId().toString(), 1, TimeUnit.HOURS);

        emailService.sendVerificationEmail(user.getEmail(), token);
    }
    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .filter(User::isVerified)
                .orElseThrow(() -> new IllegalStateException("No verified account found for this email."));
        String token = UUID.randomUUID().toString();
        String redisKey = "password_reset_token:" + token;
        redisTemplate.opsForValue().set(redisKey, user.getId().toString(), 1, TimeUnit.HOURS);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    public void confirmPasswordReset(String token, String newPassword) {
        String redisKey = "password_reset_token:" + token;
        String userId = redisTemplate.opsForValue().get(redisKey);

        if (userId == null) {
            throw new IllegalStateException("Password reset token is invalid or has expired.");
        }
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalStateException("User not found."));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redisTemplate.delete(redisKey);
    }

    @Override
    public UserResponse registerStoreAdmin(RegisterRequest request) {
        // This method is correct and unchanged
        userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
            if (existingUser.isVerified()) {
                throw new IllegalStateException("Email is already in use.");
            }
            throw new IllegalStateException("This email has been registered but not verified.");
        });

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setFullName(request.getFullName());
        newUser.setVerified(false);
        String randomPassword = UUID.randomUUID().toString();
        newUser.setPassword(passwordEncoder.encode(randomPassword));
        userRepository.save(newUser);

        newUser.setRole(UserRole.MANAGER); // set role to manager

        userRepository.save(newUser);

        String token = UUID.randomUUID().toString();
        String redisKey = "verification_token:" + token;
        redisTemplate.opsForValue().set(redisKey, newUser.getId().toString(), 1, TimeUnit.HOURS);

        emailService.sendVerificationEmail(newUser.getEmail(), token);

        return new UserResponse(newUser);
    }
}
