package com.pwdk.grocereach.User.Presentation.Controllers; // Or your correct controllers package

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Infrastructure.Securities.CustomUserDetails;
import com.pwdk.grocereach.User.Application.Services.AddressService;
import com.pwdk.grocereach.Auth.Application.Services.UserService;
import com.pwdk.grocereach.User.Presentation.Dto.AddressRequest;
import com.pwdk.grocereach.User.Presentation.Dto.AddressResponse;
import com.pwdk.grocereach.Auth.Presentation.Dto.UserResponse;
import com.pwdk.grocereach.User.Presentation.Dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
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

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUserProfile(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        String userEmail = ((CustomUserDetails) userService.loadUserById(UUID.fromString(authentication.getName()))).getUser().getEmail();
        UserResponse updatedUser = userService.updateUserProfile(userEmail, request);
        return ResponseEntity.ok(updatedUser);
    }
}
