package com.hairtrack.transformation.dto;

import com.hairtrack.transformation.entity.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileRequest {
    private String name;
    private LocalDate transplantDate;
    private Integer totalGrafts;
    private User.TransplantMethod method;
    private String zones;
    private String medications;
    private Boolean previousSession;
    private Integer previousGrafts;
}
