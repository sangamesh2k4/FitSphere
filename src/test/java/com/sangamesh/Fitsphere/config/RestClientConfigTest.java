package com.sangamesh.Fitsphere.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

class RestClientConfigTest {

    @Test
    void restClient_shouldCreateRestClient() {

        RestClientConfig config = new RestClientConfig();

        RestClient restClient = config.restClient();

        assertNotNull(restClient);
    }
}