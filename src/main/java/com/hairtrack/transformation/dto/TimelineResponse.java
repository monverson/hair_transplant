package com.hairtrack.transformation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimelineResponse {
    private UUID userId;
    private LocalDate transplantDate;
    private Integer totalPhotos;
    private List<TimelineGroup> timeline;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimelineGroup {
        private Integer month;          // 0, 1, 2, 3, ...
        private String label;           // "Day 0", "Month 1", "Month 6"
        private List<PhotoResponse> photos;
    }
}