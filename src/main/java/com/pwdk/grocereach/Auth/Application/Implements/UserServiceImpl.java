package com.pwdk.grocereach.Auth.Application.Implements;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.pwdk.grocereach.Auth.Infrastructure.specifications.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pwdk.grocereach.Auth.Application.Services.EmailService;
import com.pwdk.grocereach.Auth.Application.Services.UserService;
import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.Auth.Infrastructure.Securities.CustomUserDetails;
import com.pwdk.grocereach.Auth.Presentation.Dto.UpdateUserRequest;
import com.pwdk.grocereach.Auth.Presentation.Dto.UserResponse;
import com.pwdk.grocereach.User.Presentation.Dto.UpdateEmailRequest;
import com.pwdk.grocereach.User.Presentation.Dto.UpdateProfileRequest;
import com.pwdk.grocereach.User.Presentation.Dto.ChangePasswordRequest;
import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.image.applications.CloudinaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    private final EmailService emailService; // Inject EmailService
    private final RedisTemplate<String, String> redisTemplate; // Inject RedisTemplate


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new CustomUserDetails(user);
    }

    @Override
    public UserResponse updateUserProfile(String userId, UpdateProfileRequest request, MultipartFile profileImage) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(profileImage, "user_profiles");
            if (imageUrl != null) {
                user.setPhotoUrl(imageUrl);
            }
        }
        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser);
    }

    @Override
    public UserDetails loadUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
        return new CustomUserDetails(user);
    }

    @Override
    public PaginatedResponse<UserResponse> getAllUser(Pageable pageable, UserRole role, String search) {
        Page<User> page = userRepository.findAll(UserSpecification.getFilteredUsers(search, role), pageable);

        List<UserResponse> filteredResponses = page.getContent().stream()
                .map(UserResponse::new)
                .toList();

        return PaginatedResponse.Utils.from(page, filteredResponses);
    }

    @Override
    public void deleteStoreAdmin(UUID userID) {
        Optional<User> user = userRepository.findById(userID);
        UserRole role;

        if (user.isPresent()) {
            role = user.get().getRole();

            if (role.equals(UserRole.MANAGER)) {
                user.get().setDeletedAt(LocalDateTime.now());
                userRepository.save(user.get());
            }
        }
    }

    @Override
    public UserResponse updateStoreAdmin(UUID userID, UpdateUserRequest request) {
        User user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found!"));
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        userRepository.save(user);
        return new UserResponse(user);
    }

    @Override
    public void requestEmailChange(String currentUserEmail, UpdateEmailRequest request) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password.");
        }

        if (userRepository.findByEmail(request.getNewEmail()).isPresent()) {
            throw new IllegalStateException("The new email address is already in use by another account.");
        }
        String token = UUID.randomUUID().toString();
        String redisKey = "email_change_token:" + token;
        String redisValue = user.getId().toString() + ":" + request.getNewEmail();

        redisTemplate.opsForValue().set(redisKey, redisValue, 1, TimeUnit.HOURS); // Token expires in 1 hour

        emailService.sendVerificationEmail(user.getFullName(),token
        );
    }

    @Override
    public void changePassword(String userId, ChangePasswordRequest request) {
        System.out.println("Attempting to change password for user ID: " + userId);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        System.out.println("User found: " + user.getEmail());

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);

        userRepository.save(user);
    }
}