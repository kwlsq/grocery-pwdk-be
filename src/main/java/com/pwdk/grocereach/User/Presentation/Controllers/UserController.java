package com.pwdk.grocereach.User.Presentation.Controllers; // Or your correct controllers package

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pwdk.grocereach.User.Presentation.Dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pwdk.grocereach.Auth.Application.Services.UserService;
import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;
import com.pwdk.grocereach.Auth.Infrastructure.Securities.CustomUserDetails;
import com.pwdk.grocereach.Auth.Presentation.Dto.UserResponse;
import com.pwdk.grocereach.User.Application.Services.AddressService;
import com.pwdk.grocereach.common.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AddressService addressService;


    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String userId = authentication.getName();
        UserDetails userDetails = userService.loadUserById(UUID.fromString(userId));
        User user = ((CustomUserDetails) userDetails).getUser();
        return ResponseEntity.ok(new UserResponse(user));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressResponse> addAddress(Authentication authentication, @Valid @RequestBody AddressRequest request) {
        String userEmail = ((CustomUserDetails) userService.loadUserById(UUID.fromString(authentication.getName()))).getUser().getEmail();
        AddressResponse newAddress = addressService.createAddress(userEmail, request);
        return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(Authentication authentication) {
        String userEmail = ((CustomUserDetails) userService.loadUserById(UUID.fromString(authentication.getName()))).getUser().getEmail();
        List<AddressResponse> addresses = addressService.getUserAddresses(userEmail);
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(Authentication authentication, @PathVariable UUID addressId, @Valid @RequestBody AddressRequest request) {
        String userEmail = ((CustomUserDetails) userService.loadUserById(UUID.fromString(authentication.getName()))).getUser().getEmail();
        AddressResponse updatedAddress = addressService.updateAddress(userEmail, addressId, request);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(Authentication authentication, @PathVariable UUID addressId) {
        String userEmail = ((CustomUserDetails) userService.loadUserById(UUID.fromString(authentication.getName()))).getUser().getEmail();
        addressService.deleteAddress(userEmail, addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/me", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserResponse> updateUserProfile(
            Authentication authentication,
            @RequestPart(value = "request", required = false) UpdateProfileRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        String userId = authentication.getName();
        UpdateProfileRequest profileRequest = (request != null) ? request : new UpdateProfileRequest();

        UserResponse updatedUser = userService.updateUserProfile(userId, profileRequest, profileImage);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUser(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "12") int size,
                                        @RequestParam(value = "role", required = false) UserRole role,
                                        @RequestParam(value = "search", defaultValue = "") String search,
                                        @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                        @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sortBy, sortDirection)));

        return Response.successfulResponse(
                "Successfully fetch all user",
                userService.getAllUser(pageable, role)
        );
    }

    private Sort.Order getSortOrder(String sortBy, String sortDirection) {
        return Sort.Order.by(mapSortBy(sortBy)).with(validateSortDirection(sortDirection));
    }

    private Sort.Direction validateSortDirection(String sortDirection) {
        return com.pwdk.grocereach.store.presentations.StoreRestController.getDirection(sortDirection);
    }

    private String mapSortBy(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "createdAt";
        }
        String normalized = sortBy.trim().toLowerCase();
        return switch (normalized) {
            case "email" -> "email";
            case "name", "full_name", "fullName" -> "fullName";
            case "role" -> "role";
            default -> sortBy;
        };
    }

    @DeleteMapping("/store-admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeStoreAdmin(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        userService.deleteStoreAdmin(uuid);
        return Response.successfulResponse("Successfully remove store admin");
    }

    @PostMapping("/me/change-email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> requestEmailChange(Authentication authentication, @Valid @RequestBody UpdateEmailRequest request) {
        userService.requestEmailChange(authentication.getName(), request);
        return ResponseEntity.ok("A verification link has been sent to your new email address. Please check your inbox to confirm the change.");
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        try {
            String userId = authentication.getName();
            System.out.println("User ID from authentication: " + userId);

            userService.changePassword(userId, request);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));

        } catch (RuntimeException e) {
            System.out.println("RuntimeException: " + e.getMessage());
            return ResponseEntity.status(404)
                    .body(Map.of("message", e.getMessage()));

        } catch (Exception e) {
            System.out.println("General Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Failed to change password"));
        }
    }}