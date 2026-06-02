package com.hairtrack.transformation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class ComparisonRequest {

    @NotNull(message = "Before photo ID is required")
    private UUID beforePhotoId;

    @NotNull(message = "After photo ID is required")
    private UUID afterPhotoId;

    @Pattern(regexp = "tr|en", message = "Language must be 'tr' or 'en'")
    private String language;
}