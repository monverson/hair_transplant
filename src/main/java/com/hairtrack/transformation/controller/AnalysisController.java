package com.hairtrack.transformation.controller;

import com.hairtrack.transformation.dto.AnalysisRequest;
import com.hairtrack.transformation.dto.AnalysisResponse;
import com.hairtrack.transformation.entity.Analysis;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.UserRepository;
import com.hairtrack.transformation.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<AnalysisResponse> analyze(
            @Valid @RequestBody AnalysisRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        Analysis analysis = analysisService.analyzePhoto(request.getPhotoId(), userId, request.getLanguage());
        return ResponseEntity.ok(toResponse(analysis));
    }

    @GetMapping
    public ResponseEntity<List<AnalysisResponse>> getMyAnalyses(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        List<AnalysisResponse> analyses = analysisService.getUserAnalyses(userId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/latest")
    public ResponseEntity<AnalysisResponse> getLatestAnalysis(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = getUserId(userDetails);
        return analysisService.getLatestAnalysis(userId)
                .map(a -> ResponseEntity.ok(toResponse(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    private AnalysisResponse toResponse(Analysis analysis) {
        return AnalysisResponse.builder()
                .id(analysis.getId())
                .photoId(analysis.getPhoto() != null ? analysis.getPhoto().getId() : null)
                .densityScore(analysis.getDensityScore())
                .hairlineScore(analysis.getHairlineScore())
                .crownScore(analysis.getCrownScore())
                .templeScore(analysis.getTempleScore())
                .shockLossStatus(analysis.getShockLossStatus())
                .stageAssessment(analysis.getStageAssessment())
                .recommendation(analysis.getRecommendation())
                .rawAnalysis(analysis.getRawAnalysis())
                .monthsPostOp(analysis.getMonthsPostOp())
                .createdAt(analysis.getCreatedAt())
                .build();
    }
}