package com.sangamesh.Fitsphere.util.healthanalysis;

import com.sangamesh.Fitsphere.enums.ActivityLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public final class WaterCalculator {

    private WaterCalculator() {
    }

    public static double calculate(
            double weight,
            ActivityLevel activityLevel) {

        double water = weight * 35; // ml per kg

        switch (activityLevel) {

            case LIGHTLY_ACTIVE -> water += 300;

            case MODERATELY_ACTIVE -> water += 600;

            case VERY_ACTIVE -> water += 900;

            case EXTRA_ACTIVE -> water += 1200;

            default -> {
            }
        }

        return Math.round(water);
    }
}