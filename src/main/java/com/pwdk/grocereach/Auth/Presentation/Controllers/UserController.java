package com.pwdk.grocereach.Auth.Presentation.Controllers;

import com.pwdk.grocereach.Auth.Application.Services.UserService;
import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Infrastructure.Securities.CustomUserDetails;
import com.pwdk.grocereach.Auth.Presentation.Dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    // We need the UserService to look up the user
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserDetails userDetails = userService.loadUserByUsername(email);
        User user = ((CustomUserDetails) userDetails).getUser();
        return ResponseEntity.ok(new UserResponse(user));
    }
}