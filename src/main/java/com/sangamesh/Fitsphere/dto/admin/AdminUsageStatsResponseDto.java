package com.sangamesh.Fitsphere.dto.admin;

import lombok.Data;

@Data
public class AdminUsageStatsResponseDto {

    private Long totalUsers;

    private Long totalFoodLogs;

    private Long todayFoodLogs;

    private Long totalExercises;

    private Long activeExercises;

    private Long disabledExercises;
    private Long todayLoginRequests;

    private Long todayRegistrationRequests;

    private Long todayNutritionRequests;

    private Long todayYoutubeRequests;

}