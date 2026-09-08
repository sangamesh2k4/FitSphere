package com.sangamesh.Fitsphere.util.healthanalysis;

import com.sangamesh.Fitsphere.enums.Gender;
import com.sangamesh.Fitsphere.enums.Goal;

import static com.sangamesh.Fitsphere.enums.Goal.*;

public final class HealthInsightGenerator {

        public static String calculate(double heightCm) {

            double heightM = heightCm / 100.0;

            double min = 18.5 * heightM * heightM;
            double max = 24.9 * heightM * heightM;

            return String.format("%.1f kg - %.1f kg", min, max);
        }

    public static String getIdealRange(Gender gender) {

        if (gender == Gender.MALE) {
            return "10% - 18%";
        }

        return "18% - 28%";
    }

    public static String generate(
            Goal goal,
            double calories) {

        return switch (goal) {

            case FAT_LOSS ->
                    String.format(
                            "Consume approximately %.0f kcal/day for gradual fat loss.",
                            calories);

            case MAINTAIN ->
                    String.format(
                            "Consume approximately %.0f kcal/day to maintain your current weight.",
                            calories);

            case MUSCLE_GAIN ->
                    String.format(
                            "Consume approximately %.0f kcal/day to support lean muscle gain.",
                            calories);
        };
    }
    }
