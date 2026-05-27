package com.hairtrack.transformation.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ComparisonRequest {
    private UUID beforePhotoId;
    private UUID afterPhotoId;
    private String language;
}