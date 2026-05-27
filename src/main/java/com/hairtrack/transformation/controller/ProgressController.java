package com.hairtrack.transformation.controller;

import com.hairtrack.transformation.dto.ProgressSummaryResponse;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.UserRepository;
import com.hairtrack.transformation.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final UserRepository userRepository;

    @GetMapping("/summary")
    public ResponseEntity<ProgressSummaryResponse> getSummary(
            @RequestParam(value = "language", defaultValue = "en") String language,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        return ResponseEntity.ok(progressService.getSummary(userId, language));
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}