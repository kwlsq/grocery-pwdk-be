package com.pwdk.grocereach.User.Presentation.Controllers; // Or your correct controllers package

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;
import com.pwdk.grocereach.Auth.Infrastructure.Securities.CustomUserDetails;
import com.pwdk.grocereach.User.Application.Services.AddressService;
import com.pwdk.grocereach.Auth.Application.Services.UserService;
import com.pwdk.grocereach.User.Presentation.Dto.AddressRequest;
import com.pwdk.grocereach.User.Presentation.Dto.AddressResponse;
import com.pwdk.grocereach.Auth.Presentation.Dto.UserResponse;
import com.pwdk.grocereach.User.Presentation.Dto.UpdateProfileRequest;
import com.pwdk.grocereach.common.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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
                                        @RequestParam(value = "role", defaultValue = "") UserRole role) {
        Pageable pageable = PageRequest.of(page, size);

        return Response.successfulResponse(
            "Successfully fetch all user",
            userService.getAllUser(pageable, role)
        );
    }

    @DeleteMapping("/store-admin/:{id}")
    public ResponseEntity<?> removeStoreAdmin(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        userService.deleteStoreAdmin(uuid);
        return Response.successfulResponse("Successfully remove store admin");
    }
}
