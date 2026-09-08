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
import com.sangamesh.Fitsphere.service.HealthAnalysisService;
import com.sangamesh.Fitsphere.service.UserService;
import com.sangamesh.Fitsphere.util.healthanalysis.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthAnalysisServiceImpl implements HealthAnalysisService {

    private final UserService userService;

    private final UserProfileRepository userProfileRepository;

    private final UserProfileMapper userProfileMapper;

    @Override
    public HealthAssessmentResponseDto analyzeHealth(HealthAssessmentRequestDto request) {
        HealthAssessmentResponseDto dto = new HealthAssessmentResponseDto();
        // BMI
        double bmi = BmiCalculator.calculate(request.getWeight(), request.getHeight());
        dto.setBmi(bmi);
        dto.setBmiCategory(BmiCalculator.getCategory(bmi));
        // Body Fat
        double bodyFat = BodyFatCalculator.calculate(request.getGender(), request.getAge(), request.getHeight(), request.getWeight());
        dto.setBodyFatPercentage(bodyFat);
        dto.setBodyFatCategory(BodyFatCalculator.getCategory(request.getGender(),bodyFat));
        // BMR
        double bmr = BmrCalculator.calculate(request.getGender(), request.getWeight(), request.getHeight(), request.getAge());
        dto.setBmr(bmr);
        // TDEE
        double tdee = TdeeCalculator.calculate(bmr, request.getActivityLevel());
        dto.setTdee(tdee);
        // Calories
        double calories = CalorieCalculator.calculate(tdee, request.getGoal());
        dto.setRecommendedCalories(calories);
        // Macros
        dto.setRecommendedMacros(MacroCalculator.calculate(request.getWeight(), calories, request.getGoal()));

        // Water
        double water = WaterCalculator.calculate(request.getWeight(),request.getActivityLevel());
        dto.setRecommendedWater(water);
        // Health Score
        int score = HealthScoreCalculator.calculate(bmi, bodyFat, request.getActivityLevel());
        dto.setHealthScore(score);
        // Recommendations
        dto.setRecommendations(RecommendationGenerator.generate(bmi, bodyFat, water, score));
        dto.setHealthyWeightRange(HealthInsightGenerator.calculate(request.getHeight()));
        dto.setIdealBodyFatRange(HealthInsightGenerator.getIdealRange(request.getGender()));
        dto.setCalorieGoalSummary(HealthInsightGenerator.generate(request.getGoal(), calories));
        return dto;
    }

    @Override
    public UserProfileResponseDto createProfile(
            UserProfileRequestDto request) {

        User user = userService.getCurrentUser();

        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElse(new UserProfile());

        profile.setUser(user);

        // request -> assessment
        HealthAssessmentRequestDto assessment =
                new HealthAssessmentRequestDto();

        assessment.setAge(request.getAge());
        assessment.setGender(request.getGender());
        assessment.setHeight(request.getHeight());
        assessment.setWeight(request.getWeight());
        assessment.setActivityLevel(request.getActivityLevel());
        assessment.setGoal(request.getGoal());

        HealthAssessmentResponseDto analysis =
                analyzeHealth(assessment);

        // user inputs
        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setHeight(request.getHeight());
        profile.setWeight(request.getWeight());
        profile.setActivityLevel(request.getActivityLevel());
        profile.setGoal(request.getGoal());

        // calculated values
        profile.setBmi(analysis.getBmi());
        profile.setBmiCategory(analysis.getBmiCategory());

        profile.setBodyFatPercentage(
                analysis.getBodyFatPercentage());

        profile.setBodyFatCategory(
                analysis.getBodyFatCategory());

        profile.setBmr(analysis.getBmr());
        profile.setTdee(analysis.getTdee());

        profile.setRecommendedCalories(
                analysis.getRecommendedCalories());

        profile.setRecommendedProtein(
                analysis.getRecommendedMacros().getProtein());

        profile.setRecommendedCarbohydrates(
                analysis.getRecommendedMacros().getCarbohydrates());

        profile.setRecommendedFat(
                analysis.getRecommendedMacros().getFat());

        profile.setRecommendedWater(
                analysis.getRecommendedWater());

        profile.setHealthScore(
                analysis.getHealthScore());

        profile.setHealthStatus(
                analysis.getBmiCategory());

        profile = userProfileRepository.save(profile);

        return userProfileMapper.toResponse(profile);
    }

    @Override
    public UserProfileResponseDto updateProfile(
            UserProfileRequestDto request) {

        User user = userService.getCurrentUser();

        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found"));

        // Convert request to assessment
        HealthAssessmentRequestDto assessment =
                new HealthAssessmentRequestDto();

        assessment.setAge(request.getAge());
        assessment.setGender(request.getGender());
        assessment.setHeight(request.getHeight());
        assessment.setWeight(request.getWeight());
        assessment.setActivityLevel(request.getActivityLevel());
        assessment.setGoal(request.getGoal());

        // Recalculate everything
        HealthAssessmentResponseDto analysis =
                analyzeHealth(assessment);

        // User fields
        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setHeight(request.getHeight());
        profile.setWeight(request.getWeight());
        profile.setActivityLevel(request.getActivityLevel());
        profile.setGoal(request.getGoal());

        // Calculated fields
        profile.setBmi(analysis.getBmi());
        profile.setBmiCategory(analysis.getBmiCategory());

        profile.setBodyFatPercentage(
                analysis.getBodyFatPercentage());

        profile.setBodyFatCategory(
                analysis.getBodyFatCategory());

        profile.setBmr(analysis.getBmr());

        profile.setTdee(analysis.getTdee());

        profile.setRecommendedCalories(
                analysis.getRecommendedCalories());

        profile.setRecommendedProtein(
                analysis.getRecommendedMacros().getProtein());

        profile.setRecommendedCarbohydrates(
                analysis.getRecommendedMacros().getCarbohydrates());

        profile.setRecommendedFat(
                analysis.getRecommendedMacros().getFat());

        profile.setRecommendedWater(
                analysis.getRecommendedWater());

        profile.setHealthScore(
                analysis.getHealthScore());

        profile.setHealthStatus(
                analysis.getBmiCategory());

        profile = userProfileRepository.save(profile);

        return userProfileMapper.toResponse(profile);
    }

    public UserProfileResponseDto getMyProfile(){
        User user = userService.getCurrentUser();

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return userProfileMapper.toResponse(profile);
    }
}