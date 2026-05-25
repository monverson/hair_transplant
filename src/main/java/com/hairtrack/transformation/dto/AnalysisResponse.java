package com.hairtrack.transformation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResponse {
    private double densityScore;
    private String stageAssessment;
    private String shockLossStatus;
    private String recommendation;
    private String rawAnalysis;
}
