package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.health.HealthAssessmentRequestDto;
import com.sangamesh.Fitsphere.dto.health.HealthAssessmentResponseDto;
import com.sangamesh.Fitsphere.dto.profile.UserProfileRequestDto;
import com.sangamesh.Fitsphere.dto.profile.UserProfileResponseDto;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.UserProfile;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.mapper.UserProfileMapper;
import com.sangamesh.Fitsphere.repository.UserProfileRepository;
import com.sangamesh.Fitsphere.service.UserService;
import com.sangamesh.Fitsphere.enums.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthAnalysisServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private HealthAnalysisServiceImpl healthAnalysisService;

    @AfterEach
    void clearMocks() {
        clearInvocations(
                userService,
                userProfileRepository,
                userProfileMapper
        );
    }

    @Test
    void analyzeHealth_success() {

        HealthAssessmentRequestDto request = new HealthAssessmentRequestDto();

        request.setAge(25);
        request.setGender(Gender.MALE);
        request.setHeight(175.0);
        request.setWeight(70.0);
        request.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        request.setGoal(Goal.MAINTAIN);

        HealthAssessmentResponseDto result =
                healthAnalysisService.analyzeHealth(request);

        assertNotNull(result);

        assertTrue(result.getBmi() > 0);
        assertNotNull(result.getBmiCategory());

        assertTrue(result.getBodyFatPercentage() >= 0);
        assertNotNull(result.getBodyFatCategory());

        assertTrue(result.getBmr() > 0);
        assertTrue(result.getTdee() > 0);
        assertTrue(result.getRecommendedCalories() > 0);

        assertNotNull(result.getRecommendedMacros());

        assertTrue(result.getRecommendedWater() > 0);

        assertTrue(result.getHealthScore() >= 0);
        assertTrue(result.getHealthScore() <= 100);

        assertNotNull(result.getRecommendations());
        assertNotNull(result.getHealthyWeightRange());
        assertNotNull(result.getIdealBodyFatRange());
        assertNotNull(result.getCalorieGoalSummary());
    }

    @Test
    void createProfile_createsNewProfile() {

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        UserProfile profile = new UserProfile();

        UserProfileResponseDto response =
                new UserProfileResponseDto();

        UserProfileRequestDto request =
                new UserProfileRequestDto();

        request.setAge(25);
        request.setGender(Gender.MALE);
        request.setHeight(175.0);
        request.setWeight(70.0);
        request.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        request.setGoal(Goal.MAINTAIN);

        when(userService.getCurrentUser()).thenReturn(user);
        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.empty());

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(profile);

        when(userProfileMapper.toResponse(profile))
                .thenReturn(response);

        UserProfileResponseDto result =
                healthAnalysisService.createProfile(request);

        assertSame(response, result);

        verify(userService).getCurrentUser();
        verify(userProfileRepository).findByUser(user);
        verify(userProfileRepository).save(any(UserProfile.class));
        verify(userProfileMapper).toResponse(profile);
    }

    @Test
    void createProfile_updatesExistingProfile() {

        User user = new User();
        user.setId(1L);

        UserProfile profile = new UserProfile();

        UserProfileResponseDto response =
                new UserProfileResponseDto();

        UserProfileRequestDto request =
                new UserProfileRequestDto();

        request.setAge(30);
        request.setGender(Gender.MALE);
        request.setHeight(180.0);
        request.setWeight(80.0);
        request.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        request.setGoal(Goal.MUSCLE_GAIN);

        when(userService.getCurrentUser()).thenReturn(user);
        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(userProfileRepository.save(profile))
                .thenReturn(profile);

        when(userProfileMapper.toResponse(profile))
                .thenReturn(response);

        UserProfileResponseDto result =
                healthAnalysisService.createProfile(request);

        assertSame(response, result);

        assertSame(user, profile.getUser());
        assertEquals(30, profile.getAge());
        assertEquals(180, profile.getHeight());
        assertEquals(80, profile.getWeight());
        assertEquals(Gender.MALE, profile.getGender());
        assertEquals(ActivityLevel.MODERATELY_ACTIVE, profile.getActivityLevel());
        assertEquals(Goal.MUSCLE_GAIN, profile.getGoal());

        verify(userProfileRepository).findByUser(user);
        verify(userProfileRepository).save(profile);
    }

    @Test
    void updateProfile_success() {

        User user = new User();
        user.setId(1L);

        UserProfile profile = new UserProfile();

        UserProfileResponseDto response =
                new UserProfileResponseDto();

        UserProfileRequestDto request =
                new UserProfileRequestDto();

        request.setAge(28);
        request.setGender(Gender.MALE);
        request.setHeight(178.0);
        request.setWeight(75.0);
        request.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        request.setGoal(Goal.FAT_LOSS);

        when(userService.getCurrentUser()).thenReturn(user);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(userProfileRepository.save(profile))
                .thenReturn(profile);

        when(userProfileMapper.toResponse(profile))
                .thenReturn(response);

        UserProfileResponseDto result =
                healthAnalysisService.updateProfile(request);

        assertSame(response, result);

        assertEquals(28, profile.getAge());
        assertEquals(178, profile.getHeight());
        assertEquals(75, profile.getWeight());
        assertEquals(Gender.MALE, profile.getGender());
        assertEquals(ActivityLevel.MODERATELY_ACTIVE,
                profile.getActivityLevel());
        assertEquals(Goal.FAT_LOSS,
                profile.getGoal());

        verify(userProfileRepository).findByUser(user);
        verify(userProfileRepository).save(profile);
        verify(userProfileMapper).toResponse(profile);
    }

    @Test
    void updateProfile_throwsWhenProfileDoesNotExist() {

        User user = new User();
        user.setId(1L);

        UserProfileRequestDto request =
                new UserProfileRequestDto();

        when(userService.getCurrentUser()).thenReturn(user);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> healthAnalysisService.updateProfile(request)
        );

        verify(userProfileRepository).findByUser(user);
        verify(userProfileRepository, never())
                .save(any(UserProfile.class));
    }

    @Test
    void getMyProfile_success() {

        User user = new User();
        user.setId(1L);

        UserProfile profile = new UserProfile();

        UserProfileResponseDto response =
                new UserProfileResponseDto();

        when(userService.getCurrentUser()).thenReturn(user);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(userProfileMapper.toResponse(profile))
                .thenReturn(response);

        UserProfileResponseDto result =
                healthAnalysisService.getMyProfile();

        assertSame(response, result);

        verify(userService).getCurrentUser();
        verify(userProfileRepository).findByUser(user);
        verify(userProfileMapper).toResponse(profile);
    }

    @Test
    void getMyProfile_throwsWhenProfileDoesNotExist() {

        User user = new User();
        user.setId(1L);

        when(userService.getCurrentUser()).thenReturn(user);

        when(userProfileRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> healthAnalysisService.getMyProfile()
        );

        verify(userProfileRepository).findByUser(user);
        verify(userProfileMapper, never())
                .toResponse(any(UserProfile.class));
    }
}