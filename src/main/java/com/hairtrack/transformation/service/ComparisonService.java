package com.hairtrack.transformation.service;

import com.hairtrack.transformation.dto.ComparisonResponse;
import com.hairtrack.transformation.entity.Analysis;
import com.hairtrack.transformation.entity.Photo;
import com.hairtrack.transformation.exception.AccessDeniedException;
import com.hairtrack.transformation.exception.ResourceNotFoundException;
import com.hairtrack.transformation.repository.AnalysisRepository;
import com.hairtrack.transformation.repository.PhotoRepository;
import com.hairtrack.transformation.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComparisonService {

    private final PhotoRepository photoRepository;
    private final AnalysisRepository analysisRepository;
    private final StorageService storageService;
    private final AnalysisService analysisService;

    public ComparisonResponse comparePhotos(UUID beforePhotoId, UUID afterPhotoId, UUID userId, String language) {
        Photo before = getPhoto(beforePhotoId, userId);
        Photo after = getPhoto(afterPhotoId, userId);

        // Analiz yoksa otomatik üret
        Analysis beforeAnalysis = findOrCreateAnalysis(before, userId, language);
        Analysis afterAnalysis = findOrCreateAnalysis(after, userId, language);

        ComparisonResponse.PhotoSnapshot beforeSnapshot = buildSnapshot(before, beforeAnalysis);
        ComparisonResponse.PhotoSnapshot afterSnapshot = buildSnapshot(after, afterAnalysis);

        ComparisonResponse.Changes changes = calculateChanges(beforeSnapshot, afterSnapshot, before, after);

        String summary = buildSummary(changes, language);

        return ComparisonResponse.builder()
                .before(beforeSnapshot)
                .after(afterSnapshot)
                .changes(changes)
                .summary(summary)
                .build();
    }

    private Photo getPhoto(UUID photoId, UUID userId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found:" + photoId));


        if (!photo.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have access to this photo");
        }

        return photo;
    }

    private Analysis findOrCreateAnalysis(Photo photo, UUID userId, String language) {
        // Önce mevcut analizi ara
        Analysis existing = findAnalysisForPhoto(photo);
        if (existing != null) {
            log.info("Found existing analysis for photo {}", photo.getId());
            return existing;
        }

        // Yoksa yeni analiz üret
        log.info("No analysis found for photo {}, creating new one", photo.getId());
        return analysisService.analyzePhoto(photo.getId(), userId, language);
    }

    private Analysis findAnalysisForPhoto(Photo photo) {
        return analysisRepository.findAll().stream()
                .filter(a -> a.getPhoto() != null && a.getPhoto().getId().equals(photo.getId()))
                .findFirst()
                .orElse(null);
    }

    private ComparisonResponse.PhotoSnapshot buildSnapshot(Photo photo, Analysis analysis) {
        LocalDate transplantDate = photo.getUser().getTransplantDate();

        return ComparisonResponse.PhotoSnapshot.builder()
                .photoUrl(storageService.getSignedUrl(photo.getStorageUrl()))
                .daysSinceTransplant(DateUtil.daysBetween(transplantDate, photo.getTakenAt()))
                .monthsSinceTransplant(DateUtil.monthsBetween(transplantDate, photo.getTakenAt()))
                .densityScore(analysis != null ? analysis.getDensityScore() : null)
                .hairlineScore(analysis != null ? analysis.getHairlineScore() : null)
                .crownScore(analysis != null ? analysis.getCrownScore() : null)
                .templeScore(analysis != null ? analysis.getTempleScore() : null)
                .build();
    }

    private ComparisonResponse.Changes calculateChanges(
            ComparisonResponse.PhotoSnapshot before,
            ComparisonResponse.PhotoSnapshot after,
            Photo beforePhoto,
            Photo afterPhoto
    ) {
        LocalDate transplantDate = beforePhoto.getUser().getTransplantDate();
        Integer beforeDays = DateUtil.daysBetween(transplantDate, beforePhoto.getTakenAt());
        Integer afterDays = DateUtil.daysBetween(transplantDate, afterPhoto.getTakenAt());

        Integer daysBetween = null;
        if (beforeDays != null && afterDays != null) {
            daysBetween = afterDays - beforeDays;
        }

        return ComparisonResponse.Changes.builder()
                .densityChange(diff(before.getDensityScore(), after.getDensityScore()))
                .hairlineChange(diff(before.getHairlineScore(), after.getHairlineScore()))
                .crownChange(diff(before.getCrownScore(), after.getCrownScore()))
                .templeChange(diff(before.getTempleScore(), after.getTempleScore()))
                .daysBetween(daysBetween)
                .build();
    }

    private Double diff(Double before, Double after) {
        if (before == null || after == null) return null;
        return Math.round((after - before) * 10.0) / 10.0;
    }

    private String buildSummary(ComparisonResponse.Changes changes, String language) {
        boolean isTurkish = "tr".equalsIgnoreCase(language);

        Integer daysBetween = changes.getDaysBetween();
        Double densityChange = changes.getDensityChange();

        if (densityChange == null || daysBetween == null) {
            return isTurkish
                    ? "İki foto arasında karşılaştırma yapıldı ama analiz verisi eksik."
                    : "Comparison made between two photos but analysis data is missing.";
        }

        if (isTurkish) {
            if (densityChange > 1.5) {
                return String.format("%d gün içinde belirgin gelişme. Density skoru %.1f puan arttı. Crown ve hairline'da iyileşme var.",
                        daysBetween, densityChange);
            } else if (densityChange > 0) {
                return String.format("%d gün içinde hafif gelişme. Density skoru %.1f puan arttı.",
                        daysBetween, densityChange);
            } else if (densityChange < -1.5) {
                return String.format("%d gün içinde shock loss etkisi. Density skoru %.1f puan azaldı. Bu beklenen bir durum.",
                        daysBetween, Math.abs(densityChange));
            } else {
                return String.format("%d gün içinde durağan dönem. Skorlarda belirgin değişim yok.", daysBetween);
            }
        } else {
            if (densityChange > 1.5) {
                return String.format("Significant improvement in %d days. Density score increased by %.1f points. Improvement in crown and hairline.",
                        daysBetween, densityChange);
            } else if (densityChange > 0) {
                return String.format("Slight improvement in %d days. Density score increased by %.1f points.",
                        daysBetween, densityChange);
            } else if (densityChange < -1.5) {
                return String.format("Shock loss effect in %d days. Density score decreased by %.1f points. This is expected.",
                        daysBetween, Math.abs(densityChange));
            } else {
                return String.format("Stable phase in %d days. No significant change in scores.", daysBetween);
            }
        }
    }
}