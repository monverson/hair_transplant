package com.hairtrack.transformation.controller;

import com.hairtrack.transformation.dto.PhotoResponse;
import com.hairtrack.transformation.entity.Photo;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.UserRepository;
import com.hairtrack.transformation.service.PhotoService;
import com.hairtrack.transformation.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
        User user = getUser(userDetails);
        Photo photo = photoService.uploadPhoto(user.getId(), file, angle);
        return ResponseEntity.ok(toResponse(photo, user.getTransplantDate()));
    }

    @GetMapping
    public ResponseEntity<List<PhotoResponse>> getMyPhotos(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getUser(userDetails);
        LocalDate transplantDate = user.getTransplantDate();

        List<PhotoResponse> photos = photoService.getUserPhotos(user.getId()).stream()
                .map(p -> toResponse(p, transplantDate))
                .toList();
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/{photoId}/url")
    public ResponseEntity<String> getPhotoUrl(
            @PathVariable UUID photoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(photoService.getPhotoSignedUrl(photoId, user.getId()));
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable UUID photoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getUser(userDetails);
        photoService.deletePhoto(photoId, user.getId());
        return ResponseEntity.noContent().build();
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private PhotoResponse toResponse(Photo photo, LocalDate transplantDate) {
        return PhotoResponse.builder()
                .id(photo.getId())
                .url(photoService.getPhotoSignedUrl(photo.getId(), photo.getUser().getId()))
                .angle(photo.getAngle())
                .daysSinceTransplant(DateUtil.daysBetween(transplantDate, photo.getTakenAt()))
                .monthsSinceTransplant(DateUtil.monthsBetween(transplantDate, photo.getTakenAt()))
                .takenAt(photo.getTakenAt())
                .build();
    }
}