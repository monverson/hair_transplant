package com.hairtrack.transformation.service;

import com.hairtrack.transformation.entity.Analysis;
import com.hairtrack.transformation.entity.Photo;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.AnalysisRepository;
import com.hairtrack.transformation.repository.PhotoRepository;
import com.hairtrack.transformation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final ClaudeService claudeService;
    private final StorageService storageService;

    public Analysis analyzePhoto(UUID photoId, UUID userId, String language) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        if (!photo.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        User user = photo.getUser();
        String photoBase64 = storageService.getPhotoAsBase64(photo.getStorageUrl());

        // Default to "en" if not provided
        String lang = (language != null && !language.isBlank()) ? language : "en";

        ClaudeService.AnalysisResult result = claudeService.analyzePhoto(photo, user, photoBase64, lang);

        Analysis analysis = Analysis.builder()
                .user(user)
                .photo(photo)
                .densityScore(result.getDensityScore())
                .hairlineScore(result.getHairlineScore())
                .crownScore(result.getCrownScore())
                .templeScore(result.getTempleScore())
                .shockLossStatus(result.getShockLossStatus())
                .stageAssessment(result.getStageAssessment())
                .recommendation(result.getRecommendation())
                .rawAnalysis(result.getRawAnalysis())
                .monthsPostOp(photo.getMonthsSinceTransplant())
                .build();

        Analysis saved = analysisRepository.save(analysis);
        log.info("Analysis saved - id: {}, photoId: {}, language: {}", saved.getId(), photoId, lang);
        return saved;
    }

    public List<Analysis> getUserAnalyses(UUID userId) {
        return analysisRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<Analysis> getLatestAnalysis(UUID userId) {
        return analysisRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
    }
}