package com.hairtrack.transformation.controller;

import com.hairtrack.transformation.dto.AnalysisRequest;
import com.hairtrack.transformation.dto.AnalysisResponse;
import com.hairtrack.transformation.service.ClaudeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final ClaudeService claudeService;

    @PostMapping("/analyze")
    public AnalysisResponse analyze(@RequestBody AnalysisRequest request) {
        return claudeService.analyzePhoto(request);
    }

    @GetMapping("/health")
    public String health() {
        return "Backend çalışıyor 🚀";
    }
}