package com.hairtrack.transformation.controller;

import com.hairtrack.transformation.dto.ComparisonRequest;
import com.hairtrack.transformation.dto.ComparisonResponse;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.UserRepository;
import com.hairtrack.transformation.service.ComparisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comparisons")
@RequiredArgsConstructor
public class ComparisonController {

    private final ComparisonService comparisonService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ComparisonResponse> compare(
            @RequestBody ComparisonRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        ComparisonResponse response = comparisonService.comparePhotos(
                request.getBeforePhotoId(),
                request.getAfterPhotoId(),
                userId,
                request.getLanguage()
        );
        return ResponseEntity.ok(response);
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}