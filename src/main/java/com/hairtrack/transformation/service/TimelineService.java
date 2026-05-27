package com.hairtrack.transformation.service;

import com.hairtrack.transformation.dto.PhotoResponse;
import com.hairtrack.transformation.dto.TimelineResponse;
import com.hairtrack.transformation.entity.Photo;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.PhotoRepository;
import com.hairtrack.transformation.repository.UserRepository;
import com.hairtrack.transformation.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimelineService {

    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final StorageService storageService;

    public TimelineResponse getTimeline(UUID userId, String language) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Photo> photos = photoRepository.findByUserIdOrderByTakenAtDesc(userId);
        LocalDate transplantDate = user.getTransplantDate();

        // Foto'ları aylık olarak grupla (dinamik hesaplama)
        Map<Integer, List<Photo>> photosByMonth = new TreeMap<>();
        for (Photo photo : photos) {
            Integer month = DateUtil.monthsBetween(transplantDate, photo.getTakenAt());
            int key = month != null ? month : 0;
            photosByMonth.computeIfAbsent(key, k -> new ArrayList<>()).add(photo);
        }

        // TimelineGroup listesi oluştur
        List<TimelineResponse.TimelineGroup> timeline = photosByMonth.entrySet().stream()
                .map(entry -> {
                    int month = entry.getKey();
                    List<PhotoResponse> photoResponses = entry.getValue().stream()
                            .map(p -> toPhotoResponse(p, transplantDate))
                            .collect(Collectors.toList());

                    return TimelineResponse.TimelineGroup.builder()
                            .month(month)
                            .label(buildLabel(month, language))
                            .photos(photoResponses)
                            .build();
                })
                .collect(Collectors.toList());

        return TimelineResponse.builder()
                .userId(user.getId())
                .transplantDate(user.getTransplantDate())
                .totalPhotos(photos.size())
                .timeline(timeline)
                .build();
    }

    private String buildLabel(int month, String language) {
        boolean isTurkish = "tr".equalsIgnoreCase(language);

        if (month == 0) {
            return isTurkish ? "Başlangıç" : "Day 0";
        }
        if (month == 1) {
            return isTurkish ? "1. Ay" : "Month 1";
        }
        return isTurkish ? month + ". Ay" : "Month " + month;
    }

    private PhotoResponse toPhotoResponse(Photo photo, LocalDate transplantDate) {
        return PhotoResponse.builder()
                .id(photo.getId())
                .url(storageService.getSignedUrl(photo.getStorageUrl()))
                .angle(photo.getAngle())
                .daysSinceTransplant(DateUtil.daysBetween(transplantDate, photo.getTakenAt()))
                .monthsSinceTransplant(DateUtil.monthsBetween(transplantDate, photo.getTakenAt()))
                .takenAt(photo.getTakenAt())
                .build();
    }
}