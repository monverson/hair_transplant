package com.hairtrack.transformation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComparisonResponse {
    private PhotoSnapshot before;
    private PhotoSnapshot after;
    private Changes changes;
    private String summary;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhotoSnapshot {
        private String photoUrl;
        private Integer daysSinceTransplant;
        private Integer monthsSinceTransplant;
        private Double densityScore;
        private Double hairlineScore;
        private Double crownScore;
        private Double templeScore;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Changes {
        private Double densityChange;
        private Double hairlineChange;
        private Double crownChange;
        private Double templeChange;
        private Integer daysBetween;
    }
}