package com.sangamesh.Fitsphere.client;

import com.sangamesh.Fitsphere.dto.usda.USDAFoodDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class USDAClientTest {

    @Mock
    private RestClient restClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private USDAClient usdaClient;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                usdaClient,
                "apiKey",
                "test-api-key"
        );

        ReflectionTestUtils.setField(
                usdaClient,
                "baseUrl",
                "https://api.example.com"
        );
    }

    @Test
    void searchFoods_success() {

        USDAFoodSearchResponse response =
                new USDAFoodSearchResponse();

        doReturn(requestHeadersUriSpec)
                .when(restClient)
                .get();

        doReturn(requestHeadersUriSpec)
                .when(requestHeadersUriSpec)
                .uri(
                        "https://api.example.com/foods/search" +
                                "?query=chicken&pageSize=10" +
                                "&pageNumber=1&api_key=test-api-key"
                );

        doReturn(responseSpec)
                .when(requestHeadersUriSpec)
                .retrieve();

        when(responseSpec.body(
                USDAFoodSearchResponse.class))
                .thenReturn(response);

        USDAFoodSearchResponse result =
                usdaClient.searchFoods("chicken", 1);

        assertSame(response, result);

        verify(restClient).get();

        verify(requestHeadersUriSpec).uri(
                "https://api.example.com/foods/search" +
                        "?query=chicken&pageSize=10" +
                        "&pageNumber=1&api_key=test-api-key");

        verify(responseSpec).body(
                USDAFoodSearchResponse.class);
    }

    @Test
    void searchFoods_buildsCorrectUrlForDifferentPage() {

        USDAFoodSearchResponse response =
                new USDAFoodSearchResponse();

        doReturn(requestHeadersUriSpec)
                .when(restClient)
                .get();

        doReturn(requestHeadersUriSpec)
                .when(requestHeadersUriSpec)
                .uri(
                        "https://api.example.com/foods/search" +
                                "?query=rice&pageSize=10" +
                                "&pageNumber=3&api_key=test-api-key"
                );

        doReturn(responseSpec)
                .when(requestHeadersUriSpec)
                .retrieve();

        when(responseSpec.body(
                USDAFoodSearchResponse.class))
                .thenReturn(response);

        USDAFoodSearchResponse result =
                usdaClient.searchFoods("rice", 3);

        assertSame(response, result);

        verify(requestHeadersUriSpec).uri(
                "https://api.example.com/foods/search" +
                        "?query=rice&pageSize=10" +
                        "&pageNumber=3&api_key=test-api-key");
    }

    @Test
    void searchFoods_returnsNullWhenApiReturnsNull() {

        doReturn(requestHeadersUriSpec)
                .when(restClient)
                .get();

        doReturn(requestHeadersUriSpec)
                .when(requestHeadersUriSpec)
                .uri(anyString());

        doReturn(responseSpec)
                .when(requestHeadersUriSpec)
                .retrieve();

        when(responseSpec.body(
                USDAFoodSearchResponse.class))
                .thenReturn(null);

        USDAFoodSearchResponse result =
                usdaClient.searchFoods("apple", 1);

        assertNull(result);
    }

    @Test
    void getFoodDetails_success() {

        USDAFoodDto response =
                new USDAFoodDto();

        doReturn(requestHeadersUriSpec)
                .when(restClient)
                .get();

        doReturn(requestHeadersUriSpec)
                .when(requestHeadersUriSpec)
                .uri(
                        "https://api.example.com/food/123" +
                                "?api_key=test-api-key"
                );

        doReturn(responseSpec)
                .when(requestHeadersUriSpec)
                .retrieve();

        when(responseSpec.body(USDAFoodDto.class))
                .thenReturn(response);

        USDAFoodDto result =
                usdaClient.getFoodDetails(123L);

        assertSame(response, result);

        verify(restClient).get();

        verify(requestHeadersUriSpec).uri(
                "https://api.example.com/food/123" +
                        "?api_key=test-api-key");

        verify(responseSpec)
                .body(USDAFoodDto.class);
    }

    @Test
    void getFoodDetails_returnsNullWhenApiReturnsNull() {

        doReturn(requestHeadersUriSpec)
                .when(restClient)
                .get();

        doReturn(requestHeadersUriSpec)
                .when(requestHeadersUriSpec)
                .uri(anyString());

        doReturn(responseSpec)
                .when(requestHeadersUriSpec)
                .retrieve();

        when(responseSpec.body(USDAFoodDto.class))
                .thenReturn(null);

        USDAFoodDto result =
                usdaClient.getFoodDetails(999L);

        assertNull(result);
    }
}