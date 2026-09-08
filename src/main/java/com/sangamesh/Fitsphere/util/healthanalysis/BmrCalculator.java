package com.sangamesh.Fitsphere.util.healthanalysis;

import com.sangamesh.Fitsphere.enums.Gender;

public final class BmrCalculator {

    private BmrCalculator() {
    }

    public static double calculate(
            Gender gender,
            double weight,
            double height,
            int age) {

        double bmr;

        if (gender == Gender.MALE) {
            bmr = (10 * weight)
                    + (6.25 * height)
                    - (5 * age)
                    + 5;
        } else {
            bmr = (10 * weight)
                    + (6.25 * height)
                    - (5 * age)
                    - 161;
        }

        return round(bmr);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}