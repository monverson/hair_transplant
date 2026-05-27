package com.hairtrack.transformation.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AnalysisRequest {
    private UUID photoId;
    private String language;
}
