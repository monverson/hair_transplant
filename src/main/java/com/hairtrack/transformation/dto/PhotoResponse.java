package com.hairtrack.transformation.dto;

import com.hairtrack.transformation.entity.Photo;
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
public class PhotoResponse {
    private UUID id;
    private String url;
    private Photo.PhotoAngle angle;
    private Integer daysSinceTransplant;
    private Integer monthsSinceTransplant;
    private LocalDateTime takenAt;
}