package com.sangamesh.Fitsphere.util.healthanalysis;


import com.sangamesh.Fitsphere.enums.Goal;

public final class CalorieCalculator {

    private CalorieCalculator() {
    }

    public static double calculate(double tdee, Goal goal) {

        double calories = switch (goal) {

            case FAT_LOSS -> tdee - 500;

            case MAINTAIN -> tdee;

            case MUSCLE_GAIN -> tdee + 300;
        };

        return round(calories);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
