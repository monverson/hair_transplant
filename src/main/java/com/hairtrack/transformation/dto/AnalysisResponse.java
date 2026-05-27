package com.hairtrack.transformation.dto;

import com.hairtrack.transformation.entity.Analysis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResponse {
    private UUID id;
    private UUID photoId;
    private Double densityScore;
    private Double hairlineScore;
    private Double crownScore;
    private Double templeScore;
    private Analysis.ShockLossStatus shockLossStatus;
    private String stageAssessment;
    private String recommendation;
    private String rawAnalysis;
    private Integer monthsPostOp;
    private LocalDateTime createdAt;
}
