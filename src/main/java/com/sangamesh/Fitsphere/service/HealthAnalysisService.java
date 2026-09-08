package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.health.HealthAssessmentRequestDto;
import com.sangamesh.Fitsphere.dto.health.HealthAssessmentResponseDto;
import com.sangamesh.Fitsphere.dto.profile.UserProfileRequestDto;
import com.sangamesh.Fitsphere.dto.profile.UserProfileResponseDto;

public interface HealthAnalysisService {

    UserProfileResponseDto createProfile(UserProfileRequestDto request);
    HealthAssessmentResponseDto analyzeHealth(HealthAssessmentRequestDto request);
    //public UserProfileResponseDto createOrUpdateProfile(UserProfileRequestDto request);
    UserProfileResponseDto getMyProfile();
    UserProfileResponseDto updateProfile(UserProfileRequestDto request);

}
