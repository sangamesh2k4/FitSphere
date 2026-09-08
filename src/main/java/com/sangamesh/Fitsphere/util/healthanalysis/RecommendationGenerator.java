package com.sangamesh.Fitsphere.util.healthanalysis;


import java.util.ArrayList;
import java.util.List;

public final class RecommendationGenerator {

    private RecommendationGenerator() {
    }

    public static List<String> generate(
            double bmi,
            double bodyFat,
            double water,
            int healthScore) {

        List<String> recommendations = new ArrayList<>();

        if (bmi < 18.5) {
            recommendations.add(
                    "Increase calorie intake with nutrient-dense foods.");
        }

        if (bmi >= 25) {
            recommendations.add(
                    "Aim for a moderate calorie deficit and regular exercise.");
        }

        if (bodyFat > 25) {
            recommendations.add(
                    "Include resistance training and increase protein intake.");
        }

        if (water < 2500) {
            recommendations.add(
                    "Increase your daily water intake.");
        }

        if (healthScore >= 90) {
            recommendations.add(
                    "Excellent overall health. Maintain your current lifestyle.");
        }

        if (recommendations.isEmpty()) {
            recommendations.add(
                    "Keep following a balanced diet and regular exercise.");
        }

        return recommendations;
    }
}