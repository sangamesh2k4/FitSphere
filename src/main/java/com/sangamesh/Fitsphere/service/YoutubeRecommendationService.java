package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.youtube.YoutubeVideoDto;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

    public interface YoutubeRecommendationService {

        List<YoutubeVideoDto> searchTutorials(String exerciseName);

}