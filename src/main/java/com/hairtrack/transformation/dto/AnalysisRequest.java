package com.hairtrack.transformation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class AnalysisRequest {

    @NotNull(message = "Photo ID is required")
    private UUID photoId;

    @Pattern(regexp = "tr|en", message = "Language must be 'tr' or 'en'")
    private String language;
}