package com.sangamesh.Fitsphere.client;

import com.sangamesh.Fitsphere.dto.usda.USDAFoodDto;
import com.sangamesh.Fitsphere.dto.usda.USDAFoodSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class USDAClient {

    private final RestClient restClient;

    @Value("${usda.api.key}")
    private String apiKey;

    @Value("${usda.api.base-url}")
    private String baseUrl;

    public USDAFoodSearchResponse searchFoods(String query,int page){
        String url =baseUrl+"/foods/search"+"?query="+query + "&pageSize=10"
                + "&pageNumber=" + page+"&api_key="+apiKey;
        return restClient.get().uri(url).retrieve().body(USDAFoodSearchResponse.class);
    }

    public USDAFoodDto getFoodDetails(Long fdcId) {
        String url = baseUrl + "/food/" + fdcId + "?api_key=" + apiKey;
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(USDAFoodDto.class);

    }
}