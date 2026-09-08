package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.health.HealthAssessmentRequestDto;
import com.sangamesh.Fitsphere.dto.health.HealthAssessmentResponseDto;
import com.sangamesh.Fitsphere.service.HealthAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health Analysis",
        description = "Analyze body composition and calorie requirements")
public class HealthController {

    private final HealthAnalysisService healthAnalysisService;

    @Operation(summary = "Analyze health metrics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Health assessment completed"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/assessment")
    public ResponseEntity<HealthAssessmentResponseDto> analyzeHealth(
            @RequestBody HealthAssessmentRequestDto request) {

        return ResponseEntity.ok(
                healthAnalysisService.analyzeHealth(request)
        );
    }
}