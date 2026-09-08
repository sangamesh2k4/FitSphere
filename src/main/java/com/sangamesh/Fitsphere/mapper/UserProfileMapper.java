package com.sangamesh.Fitsphere.mapper;

import com.sangamesh.Fitsphere.dto.profile.UserProfileResponseDto;
import com.sangamesh.Fitsphere.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public UserProfileResponseDto toResponse(UserProfile profile) {

        UserProfileResponseDto dto = new UserProfileResponseDto();

        dto.setAge(profile.getAge());
        dto.setGender(profile.getGender());

        dto.setHeight(profile.getHeight());
        dto.setWeight(profile.getWeight());

        dto.setActivityLevel(profile.getActivityLevel());
        dto.setGoal(profile.getGoal());

        dto.setBmi(profile.getBmi());
        dto.setBmiCategory(profile.getBmiCategory());

        dto.setBodyFatPercentage(profile.getBodyFatPercentage());
        dto.setBodyFatCategory(profile.getBodyFatCategory());

        dto.setBmr(profile.getBmr());
        dto.setTdee(profile.getTdee());

        dto.setRecommendedCalories(profile.getRecommendedCalories());

        dto.setRecommendedProtein(profile.getRecommendedProtein());
        dto.setRecommendedCarbohydrates(profile.getRecommendedCarbohydrates());
        dto.setRecommendedFat(profile.getRecommendedFat());

        dto.setRecommendedWater(profile.getRecommendedWater());

        dto.setHealthScore(profile.getHealthScore());
        dto.setHealthStatus(profile.getHealthStatus());

        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());

        return dto;
    }
}