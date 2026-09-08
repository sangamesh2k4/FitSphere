package com.sangamesh.Fitsphere.util.healthanalysis;

import com.sangamesh.Fitsphere.enums.Gender;

public final class BodyFatCalculator {

    private BodyFatCalculator() {
    }

    public static double calculate(
            Gender gender,
            int age,
            double heightCm,
            double weightKg) {

        double bmi = BmiCalculator.calculate( weightKg,heightCm);

        double bodyFat;

        if (gender == Gender.MALE) {
            bodyFat = (1.20 * bmi)
                    + (0.23 * age)
                    - 16.2;
        } else {
            bodyFat = (1.20 * bmi)
                    + (0.23 * age)
                    - 5.4;
        }

        return round(bodyFat);
    }

    public static String getCategory(
            Gender gender,
            double bodyFat) {

        if (gender == Gender.MALE) {

            if (bodyFat < 6)
                return "Essential Fat";

            if (bodyFat < 14)
                return "Athlete";

            if (bodyFat < 18)
                return "Fitness";

            if (bodyFat < 25)
                return "Average";

            return "Obese";
        }

        if (bodyFat < 14)
            return "Essential Fat";

        if (bodyFat < 21)
            return "Athlete";

        if (bodyFat < 25)
            return "Fitness";

        if (bodyFat < 32)
            return "Average";

        return "Obese";
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}