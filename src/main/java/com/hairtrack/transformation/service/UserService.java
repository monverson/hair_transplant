package com.hairtrack.transformation.service;

import com.hairtrack.transformation.dto.UserProfileRequest;
import com.hairtrack.transformation.dto.UserProfileResponse;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toResponse(user);
    }

    public UserProfileResponse updateProfile(UUID userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Sadece null olmayan field'ları güncelle (partial update)
        if (request.getName() != null) user.setName(request.getName());
        if (request.getTransplantDate() != null) user.setTransplantDate(request.getTransplantDate());
        if (request.getTotalGrafts() != null) user.setTotalGrafts(request.getTotalGrafts());
        if (request.getMethod() != null) user.setMethod(request.getMethod());
        if (request.getZones() != null) user.setZones(request.getZones());
        if (request.getMedications() != null) user.setMedications(request.getMedications());
        if (request.getPreviousSession() != null) user.setPreviousSession(request.getPreviousSession());
        if (request.getPreviousGrafts() != null) user.setPreviousGrafts(request.getPreviousGrafts());

        User saved = userRepository.save(user);
        log.info("User profile updated - id: {}", saved.getId());
        return toResponse(saved);
    }

    private UserProfileResponse toResponse(User user) {
        Integer daysSinceTransplant = null;
        Integer monthsSinceTransplant = null;

        if (user.getTransplantDate() != null) {
            LocalDate today = LocalDate.now();
            daysSinceTransplant = (int) ChronoUnit.DAYS.between(user.getTransplantDate(), today);
            monthsSinceTransplant = (int) ChronoUnit.MONTHS.between(user.getTransplantDate(), today);
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .transplantDate(user.getTransplantDate())
                .totalGrafts(user.getTotalGrafts())
                .method(user.getMethod())
                .zones(user.getZones())
                .medications(user.getMedications())
                .previousSession(user.getPreviousSession())
                .previousGrafts(user.getPreviousGrafts())
                .daysSinceTransplant(daysSinceTransplant)
                .monthsSinceTransplant(monthsSinceTransplant)
                .subscriptionStatus(user.getSubscriptionStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
