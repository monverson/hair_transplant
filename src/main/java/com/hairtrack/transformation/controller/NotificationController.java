package com.hairtrack.transformation.controller;

import com.hairtrack.transformation.dto.NotificationResponse;
import com.hairtrack.transformation.entity.Notification;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.UserRepository;
import com.hairtrack.transformation.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @RequestParam(value = "language", defaultValue = "en") String language,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);

        // Her çağrıda yeni milestone'ları kontrol et ve üret
        notificationService.checkAndCreateMilestones(userId);

        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId).stream()
                .map(n -> toResponse(n, language))
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread(
            @RequestParam(value = "language", defaultValue = "en") String language,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        notificationService.checkAndCreateMilestones(userId);

        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId).stream()
                .map(n -> toResponse(n, language))
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable UUID notificationId,
            @RequestParam(value = "language", defaultValue = "en") String language,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        Notification notification = notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(toResponse(notification, language));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    private NotificationResponse toResponse(Notification n, String language) {
        boolean isTurkish = "tr".equalsIgnoreCase(language);

        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .milestoneDays(n.getMilestoneDays())
                .title(isTurkish ? n.getTitleTr() : n.getTitleEn())
                .message(isTurkish ? n.getMessageTr() : n.getMessageEn())
                .read(n.getRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}