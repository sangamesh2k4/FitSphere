package com.sangamesh.Fitsphere.util;

public final class NutritionCalculator {

    private NutritionCalculator() {
    }

    public static double calculateFactor(Double servingSize, Double quantity) {

        double baseQuantity = (servingSize != null && servingSize > 0)
                ? servingSize
                : 100.0;

        return quantity / baseQuantity;
    }

    public static Double scale(Double value, double factor) {
        if (value == null) {
            return 0.0;
        }
        return Math.round(value * factor * 100.0) / 100.0;
    }
}
