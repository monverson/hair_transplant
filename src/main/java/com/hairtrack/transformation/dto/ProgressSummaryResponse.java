package com.hairtrack.transformation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressSummaryResponse {
    private Integer totalCheckIns;
    private LocalDateTime firstCheckIn;
    private LocalDateTime lastCheckIn;
    private Integer currentDay;
    private Integer currentMonth;
    private String currentStage;
    private ScoresTrend scoresTrend;
    private NextMilestone nextMilestone;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScoresTrend {
        private Double densityFirst;
        private Double densityLatest;
        private Double densityChange;
        private Double hairlineFirst;
        private Double hairlineLatest;
        private Double hairlineChange;
        private Double crownFirst;
        private Double crownLatest;
        private Double crownChange;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NextMilestone {
        private Integer day;
        private String label;
        private Integer daysAway;
    }
}
