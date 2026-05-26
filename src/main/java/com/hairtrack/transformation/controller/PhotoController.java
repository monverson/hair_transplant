package com.hairtrack.transformation.controller;

import com.hairtrack.transformation.dto.PhotoResponse;
import com.hairtrack.transformation.entity.Photo;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.UserRepository;
import com.hairtrack.transformation.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<PhotoResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "angle", defaultValue = "FRONT") Photo.PhotoAngle angle,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        Photo photo = photoService.uploadPhoto(userId, file, angle);
        return ResponseEntity.ok(toResponse(photo, userId));
    }

    @GetMapping
    public ResponseEntity<List<PhotoResponse>> getMyPhotos(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        List<PhotoResponse> photos = photoService.getUserPhotos(userId).stream()
                .map(p -> toResponse(p, userId))
                .toList();
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/{photoId}/url")
    public ResponseEntity<String> getPhotoUrl(
            @PathVariable UUID photoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        return ResponseEntity.ok(photoService.getPhotoSignedUrl(photoId, userId));
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable UUID photoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        photoService.deletePhoto(photoId, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    private PhotoResponse toResponse(Photo photo, UUID userId) {
        return PhotoResponse.builder()
                .id(photo.getId())
                .url(photoService.getPhotoSignedUrl(photo.getId(), userId))
                .angle(photo.getAngle())
                .daysSinceTransplant(photo.getDaysSinceTransplant())
                .monthsSinceTransplant(photo.getMonthsSinceTransplant())
                .takenAt(photo.getTakenAt())
                .build();
    }
}