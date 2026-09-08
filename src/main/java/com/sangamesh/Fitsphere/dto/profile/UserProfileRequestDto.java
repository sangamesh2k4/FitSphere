package com.sangamesh.Fitsphere.dto.profile;

import com.sangamesh.Fitsphere.enums.ActivityLevel;
import com.sangamesh.Fitsphere.enums.Gender;
import com.sangamesh.Fitsphere.enums.Goal;
import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class UserProfileRequestDto {

    @NotNull
    @Min(10)
    @Max(100)
    private Integer age;

    @NotNull
    private Gender gender;

    @NotNull
    @DecimalMin("100.0")
    @DecimalMax("250.0")
    private Double height;

    @NotNull
    @DecimalMin("20.0")
    @DecimalMax("300.0")
    private Double weight;

    @NotNull
    private ActivityLevel activityLevel;

    @NotNull
    private Goal goal;
}