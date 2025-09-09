package com.pwdk.grocereach.Auth.Application.Implements;

import com.pwdk.grocereach.Auth.Application.Services.UserService;
import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.Auth.Infrastructure.Securities.CustomUserDetails;
import com.pwdk.grocereach.Auth.Presentation.Dto.UpdateUserRequest;
import com.pwdk.grocereach.Auth.Presentation.Dto.UserResponse;
import com.pwdk.grocereach.User.Presentation.Dto.UpdateProfileRequest;
import com.pwdk.grocereach.common.PaginatedResponse;
import com.pwdk.grocereach.image.applications.CloudinaryService;
import com.pwdk.grocereach.product.presentations.dtos.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

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
    public PaginatedResponse<UserResponse> getAllUser(Pageable pageable, UserRole role) {
        Page<User> page;

        if (role != null) {
            page = userRepository.findAllVerifiedByRole(role, pageable).map(user -> user);
        } else {
            page = userRepository.findAllByVerifiedTrue(pageable);
        }

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
}