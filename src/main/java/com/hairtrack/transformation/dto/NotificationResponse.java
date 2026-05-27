package com.hairtrack.transformation.dto;

import com.hairtrack.transformation.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private UUID id;
    private Notification.NotificationType type;
    private Integer milestoneDays;
    private String title;
    private String message;
    private Boolean read;
    private LocalDateTime createdAt;
}