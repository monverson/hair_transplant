package com.hairtrack.transformation.dto;

import com.hairtrack.transformation.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String email;
    private String name;
    private LocalDate transplantDate;
    private Integer totalGrafts;
    private User.TransplantMethod method;
    private String zones;
    private String medications;
    private Boolean previousSession;
    private Integer previousGrafts;
    private Integer daysSinceTransplant;
    private Integer monthsSinceTransplant;
    private User.SubscriptionStatus subscriptionStatus;
    private LocalDateTime createdAt;
}
