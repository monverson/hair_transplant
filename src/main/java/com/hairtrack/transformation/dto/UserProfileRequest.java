package com.hairtrack.transformation.dto;

import com.hairtrack.transformation.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileRequest {

    @Size(max = 100, message = "Name must be under 100 characters")
    private String name;

    @PastOrPresent(message = "Transplant date cannot be in the future")
    private LocalDate transplantDate;

    @Min(value = 1, message = "Total grafts must be positive")
    @Max(value = 20000, message = "Total grafts seems unrealistic")
    private Integer totalGrafts;

    private User.TransplantMethod method;

    @Size(max = 200, message = "Zones field too long")
    private String zones;

    @Size(max = 200, message = "Medications field too long")
    private String medications;

    private Boolean previousSession;

    @Min(value = 0, message = "Previous grafts cannot be negative")
    @Max(value = 20000, message = "Previous grafts seems unrealistic")
    private Integer previousGrafts;
}