package com.sangamesh.Fitsphere.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.youtube.YoutubeVideoDto;
import com.sangamesh.Fitsphere.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YoutubeRecommendationServiceImplTest {

    @Mock
    private RestClient restClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ObjectMapper objectMapper;

    private YoutubeRecommendationServiceImpl service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new YoutubeRecommendationServiceImpl(
                restClient,
                objectMapper
        );

        ReflectionTestUtils.setField(
                service,
                "apiKey",
                "test-api-key"
        );

        ReflectionTestUtils.setField(
                service,
                "searchUrl",
                "https://youtube.test/search"
        );
    }

    @Test
    void searchTutorials_shouldReturnVideos() {
        String jsonResponse = """
                {
                  "items": [
                    {
                      "id": {
                        "videoId": "abc123"
                      },
                      "snippet": {
                        "title": "Bench Press Tutorial",
                        "channelTitle": "Fitness Channel",
                        "thumbnails": {
                          "high": {
                            "url": "https://img.youtube.com/thumb.jpg"
                          }
                        }
                      }
                    }
                  ]
                }
                """;

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(jsonResponse);

        List<YoutubeVideoDto> result = service.searchTutorials("Bench Press");

        assertEquals(1, result.size());

        YoutubeVideoDto video = result.get(0);
        assertEquals("abc123", video.getVideoId());
        assertEquals("Bench Press Tutorial", video.getTitle());
        assertEquals("Fitness Channel", video.getChannelTitle());
        assertEquals("https://img.youtube.com/thumb.jpg", video.getThumbnailUrl());
        assertEquals("https://www.youtube.com/watch?v=abc123", video.getVideoUrl());

        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                "https://youtube.test/search" +
                        "?part=snippet" +
                        "&q=Bench Press exercise proper form tutorial" +
                        "&type=video" +
                        "&maxResults=4" +
                        "&videoEmbeddable=true" +
                        "&safeSearch=strict" +
                        "&key=test-api-key"
        );
        verify(responseSpec).body(String.class);
    }

    @Test
    void searchTutorials_whenItemsEmpty_shouldReturnEmptyList() {
        String jsonResponse = "{\"items\":[]}";

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(jsonResponse);

        List<YoutubeVideoDto> result = service.searchTutorials("Squat");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchTutorials_whenJsonParsingFails_shouldThrowExternalApiException() {
        String invalidJsonResponse = "invalid-json-content";

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(invalidJsonResponse);

        ExternalApiException exception = assertThrows(
                ExternalApiException.class,
                () -> service.searchTutorials("Bench Press")
        );

        assertEquals("Failed to fetch YouTube recommendations", exception.getMessage());
    }

    @Test
    void searchTutorials_whenResponseIsNull_shouldThrowExternalApiException() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(null);

        ExternalApiException exception = assertThrows(
                ExternalApiException.class,
                () -> service.searchTutorials("Squat")
        );

        assertEquals("Failed to fetch YouTube recommendations", exception.getMessage());
    }

    @Test
    void searchTutorials_whenRestClientFails_shouldPropagateException() {
        when(restClient.get()).thenThrow(new RuntimeException("YouTube unavailable"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.searchTutorials("Squat")
        );

        assertEquals("YouTube unavailable", exception.getMessage());
    }
}