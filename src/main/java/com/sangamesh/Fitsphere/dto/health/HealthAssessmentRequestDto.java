package com.sangamesh.Fitsphere.dto.health;

import com.sangamesh.Fitsphere.enums.ActivityLevel;
import com.sangamesh.Fitsphere.enums.Gender;
import com.sangamesh.Fitsphere.enums.Goal;
import lombok.Data;

@Data
public class HealthAssessmentRequestDto {

    private Integer age;

    private Gender gender;

    private Double height;   // cm

    private Double weight;   // kg

    private ActivityLevel activityLevel;

    private Goal goal;
}
