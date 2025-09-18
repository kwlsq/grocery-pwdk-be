package com.pwdk.grocereach.Auth.Application.Services;

import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;
import com.pwdk.grocereach.Auth.Presentation.Dto.UpdateUserRequest;
import com.pwdk.grocereach.Auth.Presentation.Dto.UserResponse;
import com.pwdk.grocereach.User.Presentation.Dto.UpdateEmailRequest;
import com.pwdk.grocereach.User.Presentation.Dto.UpdateProfileRequest;
import com.pwdk.grocereach.common.PaginatedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {
    UserResponse updateUserProfile(String userId, UpdateProfileRequest request, MultipartFile profileImage);
    UserDetails loadUserById(UUID id);
    PaginatedResponse<UserResponse> getAllUser(Pageable pageable, UserRole role);
    void deleteStoreAdmin(UUID userID);
    UserResponse updateStoreAdmin(UUID useID, UpdateUserRequest request);
    void requestEmailChange(String currentUserEmail, UpdateEmailRequest request);

}