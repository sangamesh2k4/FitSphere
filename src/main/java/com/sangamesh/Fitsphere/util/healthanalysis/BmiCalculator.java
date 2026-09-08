package com.sangamesh.Fitsphere.util.healthanalysis;


public final class BmiCalculator {

    private BmiCalculator() {
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public static double calculate(double weight, double height) {
        double heightInMeters = height / 100.0;
        return round(weight / (heightInMeters * heightInMeters));
    }

    public static String getCategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        }

        if (bmi < 25) {
            return "Normal Weight";
        }

        if (bmi < 30) {
            return "Overweight";
        }

        if (bmi < 35) {
            return "Obesity Class I";
        }

        if (bmi < 40) {
            return "Obesity Class II";
        }

        return "Obesity Class III";
    }
}
