package com.sangamesh.Fitsphere.entity;

import com.sangamesh.Fitsphere.enums.ActivityLevel;
import com.sangamesh.Fitsphere.enums.Gender;
import com.sangamesh.Fitsphere.enums.Goal;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    void noArgsConstructor_createsEmptyEntity() {

        UserProfile profile = new UserProfile();

        assertNull(profile.getId());
        assertNull(profile.getUser());
        assertNull(profile.getAge());
        assertNull(profile.getGender());
        assertNull(profile.getHeight());
        assertNull(profile.getWeight());
        assertNull(profile.getActivityLevel());
        assertNull(profile.getGoal());

        assertNull(profile.getBmi());
        assertNull(profile.getBmiCategory());
        assertNull(profile.getBodyFatPercentage());
        assertNull(profile.getBodyFatCategory());
        assertNull(profile.getBmr());
        assertNull(profile.getTdee());
        assertNull(profile.getRecommendedCalories());
        assertNull(profile.getRecommendedProtein());
        assertNull(profile.getRecommendedCarbohydrates());
        assertNull(profile.getRecommendedFat());
        assertNull(profile.getRecommendedWater());
        assertNull(profile.getHealthScore());
        assertNull(profile.getHealthStatus());

        assertNull(profile.getCreatedAt());
        assertNull(profile.getUpdatedAt());
    }

    @Test
    void settersAndGetters_workCorrectly() {

        UserProfile profile = new UserProfile();

        User user = new User();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        profile.setId(1L);
        profile.setUser(user);
        profile.setAge(25);
        profile.setGender(Gender.MALE);
        profile.setHeight(175.0);
        profile.setWeight(70.0);
        profile.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        profile.setGoal(Goal.MAINTAIN);

        profile.setBmi(22.86);
        profile.setBmiCategory("NORMAL");
        profile.setBodyFatPercentage(15.5);
        profile.setBodyFatCategory("ATHLETIC");
        profile.setBmr(1700.0);
        profile.setTdee(2635.0);
        profile.setRecommendedCalories(2500.0);
        profile.setRecommendedProtein(140.0);
        profile.setRecommendedCarbohydrates(300.0);
        profile.setRecommendedFat(70.0);
        profile.setRecommendedWater(2.8);
        profile.setHealthScore(85);
        profile.setHealthStatus("Healthy");
        profile.setCreatedAt(createdAt);
        profile.setUpdatedAt(updatedAt);

        assertEquals(1L, profile.getId());
        assertEquals(user, profile.getUser());
        assertEquals(25, profile.getAge());
        assertEquals(Gender.MALE, profile.getGender());
        assertEquals(175.0, profile.getHeight());
        assertEquals(70.0, profile.getWeight());
        assertEquals(ActivityLevel.MODERATELY_ACTIVE,
                profile.getActivityLevel());
        assertEquals(Goal.MAINTAIN, profile.getGoal());

        assertEquals(22.86, profile.getBmi());
        assertEquals("NORMAL", profile.getBmiCategory());
        assertEquals(15.5, profile.getBodyFatPercentage());
        assertEquals("ATHLETIC", profile.getBodyFatCategory());
        assertEquals(1700.0, profile.getBmr());
        assertEquals(2635.0, profile.getTdee());
        assertEquals(2500.0, profile.getRecommendedCalories());
        assertEquals(140.0, profile.getRecommendedProtein());
        assertEquals(300.0, profile.getRecommendedCarbohydrates());
        assertEquals(70.0, profile.getRecommendedFat());
        assertEquals(2.8, profile.getRecommendedWater());
        assertEquals(85, profile.getHealthScore());
        assertEquals("Healthy", profile.getHealthStatus());
        assertEquals(createdAt, profile.getCreatedAt());
        assertEquals(updatedAt, profile.getUpdatedAt());
    }

    @Test
    void dataAnnotation_generatesEqualsAndHashCode() {

        UserProfile profile1 = new UserProfile();
        UserProfile profile2 = new UserProfile();

        profile1.setId(1L);
        profile2.setId(1L);

        assertEquals(profile1, profile2);
        assertEquals(profile1.hashCode(), profile2.hashCode());
    }
}