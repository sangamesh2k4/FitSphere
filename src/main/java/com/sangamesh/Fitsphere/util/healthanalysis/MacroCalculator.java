package com.sangamesh.Fitsphere.util.healthanalysis;


import com.sangamesh.Fitsphere.dto.profile.MacroRecommendationDto;
import com.sangamesh.Fitsphere.enums.Goal;
import com.sangamesh.Fitsphere.exception.BadRequestException;

public final class MacroCalculator {

    private MacroCalculator() {
    }

    public static MacroRecommendationDto calculate(
            double weight,
            double calories,
            Goal goal) {

        MacroRecommendationDto dto = new MacroRecommendationDto();

        double protein;
        double fat;
        double carbs;

        switch (goal) {

            case FAT_LOSS -> {

                protein = weight * 2.2;
                fat = weight * 0.8;

                carbs = (calories
                        - (protein * 4)
                        - (fat * 9)) / 4;
            }

            case MAINTAIN -> {

                protein = weight * 1.8;
                fat = weight * 1.0;

                carbs = (calories
                        - (protein * 4)
                        - (fat * 9)) / 4;
            }

            case MUSCLE_GAIN -> {

                protein = weight * 2.0;
                fat = weight * 1.0;

                carbs = (calories
                        - (protein * 4)
                        - (fat * 9)) / 4;
            }

            default -> throw new BadRequestException("Invalid goal.");
        }

        dto.setProtein(round(protein));
        dto.setFat(round(fat));
        dto.setCarbohydrates(round(carbs));

        return dto;
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}