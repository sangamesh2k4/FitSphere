package com.sangamesh.Fitsphere.util.healthanalysis;


import com.sangamesh.Fitsphere.enums.ActivityLevel;

public final class TdeeCalculator {

    private TdeeCalculator() {
    }

    public static double calculate(
            double bmr,
            ActivityLevel activityLevel) {

        double multiplier = switch (activityLevel) {

            case SEDENTARY -> 1.20;

            case LIGHTLY_ACTIVE -> 1.375;

            case MODERATELY_ACTIVE -> 1.55;

            case VERY_ACTIVE -> 1.725;

            case EXTRA_ACTIVE -> 1.90;
            case ATHLETE -> 2.0;
        };

        return round(bmr * multiplier);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
