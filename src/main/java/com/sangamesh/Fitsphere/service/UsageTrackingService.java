package com.sangamesh.Fitsphere.service;

public interface UsageTrackingService {

    void track(String feature);

    long getTodayUsage(String feature);
}
