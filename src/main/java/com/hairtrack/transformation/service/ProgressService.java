package com.hairtrack.transformation.service;

import com.hairtrack.transformation.dto.ProgressSummaryResponse;
import com.hairtrack.transformation.entity.Analysis;
import com.hairtrack.transformation.entity.User;
import com.hairtrack.transformation.repository.AnalysisRepository;
import com.hairtrack.transformation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProgressService {

    private final UserRepository userRepository;
    private final AnalysisRepository analysisRepository;

    public ProgressSummaryResponse getSummary(UUID userId, String language) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Analysis> analyses = analysisRepository.findByUserIdOrderByCreatedAtDesc(userId);

        Integer currentDay = null;
        Integer currentMonth = null;
        if (user.getTransplantDate() != null) {
            LocalDate today = LocalDate.now();
            currentDay = (int) ChronoUnit.DAYS.between(user.getTransplantDate(), today);
            currentMonth = (int) ChronoUnit.MONTHS.between(user.getTransplantDate(), today);
        }

        ProgressSummaryResponse.ScoresTrend trend = buildTrend(analyses);
        ProgressSummaryResponse.NextMilestone nextMilestone = findNextMilestone(currentDay, language);

        return ProgressSummaryResponse.builder()
                .totalCheckIns(analyses.size())
                .firstCheckIn(analyses.isEmpty() ? null : analyses.get(analyses.size() - 1).getCreatedAt())
                .lastCheckIn(analyses.isEmpty() ? null : analyses.get(0).getCreatedAt())
                .currentDay(currentDay)
                .currentMonth(currentMonth)
                .currentStage(buildStageLabel(currentDay, language))
                .scoresTrend(trend)
                .nextMilestone(nextMilestone)
                .build();
    }

    private ProgressSummaryResponse.ScoresTrend buildTrend(List<Analysis> analyses) {
        if (analyses.isEmpty()) {
            return null;
        }

        Analysis latest = analyses.get(0);
        Analysis first = analyses.get(analyses.size() - 1);

        return ProgressSummaryResponse.ScoresTrend.builder()
                .densityFirst(first.getDensityScore())
                .densityLatest(latest.getDensityScore())
                .densityChange(diff(first.getDensityScore(), latest.getDensityScore()))
                .hairlineFirst(first.getHairlineScore())
                .hairlineLatest(latest.getHairlineScore())
                .hairlineChange(diff(first.getHairlineScore(), latest.getHairlineScore()))
                .crownFirst(first.getCrownScore())
                .crownLatest(latest.getCrownScore())
                .crownChange(diff(first.getCrownScore(), latest.getCrownScore()))
                .build();
    }

    private Double diff(Double a, Double b) {
        if (a == null || b == null) return null;
        return Math.round((b - a) * 10.0) / 10.0;
    }

    private ProgressSummaryResponse.NextMilestone findNextMilestone(Integer currentDay, String language) {
        if (currentDay == null) return null;

        for (MilestoneDefinitions.Milestone m : MilestoneDefinitions.MILESTONES) {
            if (m.getDayMark() > currentDay) {
                boolean isTurkish = "tr".equalsIgnoreCase(language);
                return ProgressSummaryResponse.NextMilestone.builder()
                        .day(m.getDayMark())
                        .label(isTurkish ? m.getTitleTr() : m.getTitleEn())
                        .daysAway(m.getDayMark() - currentDay)
                        .build();
            }
        }
        return null;
    }

    private String buildStageLabel(Integer currentDay, String language) {
        if (currentDay == null) return "Setup required";

        boolean isTurkish = "tr".equalsIgnoreCase(language);

        if (currentDay <= 14) return isTurkish ? "İyileşme dönemi" : "Healing phase";
        if (currentDay <= 30) return isTurkish ? "Shock loss başlangıcı" : "Shock loss start";
        if (currentDay <= 90) return isTurkish ? "Shock loss zirvesi" : "Shock loss peak";
        if (currentDay <= 180) return isTurkish ? "Büyüme dönemi" : "Growth phase";
        if (currentDay <= 365) return isTurkish ? "Olgunlaşma" : "Maturation";
        return isTurkish ? "Final sonuç" : "Final result";
    }
}
