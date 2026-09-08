package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.profile.UserProfileRequestDto;
import com.sangamesh.Fitsphere.dto.profile.UserProfileResponseDto;
import com.sangamesh.Fitsphere.service.HealthAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Manage user profile")
public class ProfileController {

    private final HealthAnalysisService healthAnalysisService;

    @Operation(summary = "Create your user profile")
    @PostMapping
    public ResponseEntity<UserProfileResponseDto> createProfile(
            @Valid @RequestBody UserProfileRequestDto request) {

        return ResponseEntity.ok(
                healthAnalysisService.createProfile(request)
        );
    }

    @Operation(summary = "view you user profile details")
    @GetMapping
    public ResponseEntity<UserProfileResponseDto> getMyProfile() {
        return ResponseEntity.ok(
                healthAnalysisService.getMyProfile()
        );
    }

    @Operation(summary = "update or correct your user profile details")
    @PutMapping
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @Valid @RequestBody UserProfileRequestDto request) {

        return ResponseEntity.ok(
                healthAnalysisService.updateProfile(request));
    }
}