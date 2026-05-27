package com.hairtrack.transformation.controller;

import com.hairtrack.transformation.dto.UserProfileRequest;
import com.hairtrack.transformation.dto.UserProfileResponse;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.UserRepository;
import com.hairtrack.transformation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}