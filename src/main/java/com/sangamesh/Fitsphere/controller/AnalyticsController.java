package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.analytics.DashboardAnalyticsDto;
import com.sangamesh.Fitsphere.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public DashboardAnalyticsDto getDashboardAnalytics() {

        return analyticsService.getDashboardAnalytics();
    }
}