package com.hairtrack.transformation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisRequest {
    private String userId;
    private String photoBase64;
    private int monthsPostOp;
    private int totalGrafts;
    private String method;        // "DHI" or "FUE"
    private String zones;         // "hairline,crown"
    private String medications;   // "minoxidil"
}
