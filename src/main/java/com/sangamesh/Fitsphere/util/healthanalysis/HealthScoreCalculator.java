package com.sangamesh.Fitsphere.util.healthanalysis;

import com.sangamesh.Fitsphere.enums.ActivityLevel;

public final class HealthScoreCalculator {

    private HealthScoreCalculator() {
    }

    public static int calculate(
            double bmi,
            double bodyFat,
            ActivityLevel activityLevel) {

        int score = 100;

        if (bmi < 18.5 || bmi > 30)
            score -= 20;
        else if (bmi >= 25)
            score -= 10;

        if (bodyFat > 30)
            score -= 20;
        else if (bodyFat > 25)
            score -= 10;

        switch (activityLevel) {

            case SEDENTARY -> score -= 20;

            case LIGHTLY_ACTIVE -> score -= 10;

            default -> {
            }
        }

        return Math.max(score, 0);
    }
}