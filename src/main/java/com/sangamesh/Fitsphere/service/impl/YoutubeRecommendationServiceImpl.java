package com.sangamesh.Fitsphere.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.youtube.YoutubeVideoDto;
import com.sangamesh.Fitsphere.exception.ExternalApiException;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.service.YoutubeRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeRecommendationServiceImpl implements YoutubeRecommendationService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.search-url}")
    private String searchUrl;

    @Override
    @Cacheable(value = "CacheNames.EXERCISE_VIDEOS", key ="#exerciseName.trim().toLowerCase()")
    public List<YoutubeVideoDto> searchTutorials(String exerciseName) {
        String query = exerciseName + " exercise proper form tutorial";

        String url = searchUrl
                + "?part=snippet"
                + "&q=" + query
                + "&type=video"
                + "&maxResults=4"
                + "&videoEmbeddable=true"
                + "&safeSearch=strict"
                + "&key=" + apiKey;

        String response = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        try {

            JsonNode root = objectMapper.readTree(response);

            List<YoutubeVideoDto> videos = new ArrayList<>();

            for (JsonNode item : root.path("items")) {

                String videoId = item.path("id")
                        .path("videoId")
                        .asText();

                String title = item.path("snippet")
                        .path("title")
                        .asText();

                String channelTitle = item.path("snippet")
                        .path("channelTitle")
                        .asText();

                String thumbnailUrl = item.path("snippet")
                        .path("thumbnails")
                        .path("high")
                        .path("url")
                        .asText();

                videos.add(
                        YoutubeVideoDto.builder()
                                .videoId(videoId)
                                .title(title)
                                .channelTitle(channelTitle)
                                .thumbnailUrl(thumbnailUrl)
                                .videoUrl("https://www.youtube.com/watch?v=" + videoId)
                                .build()
                );
            }

            return videos;

        } catch (Exception e) {
            throw new ExternalApiException( "Failed to fetch YouTube recommendations");
        }
    }

}
