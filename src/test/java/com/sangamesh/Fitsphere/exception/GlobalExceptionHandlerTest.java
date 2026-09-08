package com.sangamesh.Fitsphere.exception;

import com.sangamesh.Fitsphere.ratelimit.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    private HttpServletRequest request() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        return request;
    }

    @Test
    void handleResourceNotFound_shouldReturn404() {

        ResourceNotFoundException ex =
                new ResourceNotFoundException("Resource not found");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleResourceNotFound(ex, request());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleDuplicateResource_shouldReturn409() {

        DuplicateResourceException ex =
                new DuplicateResourceException("Resource already exists");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleDuplicateResource(ex, request());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(409, response.getBody().getStatus());
        assertEquals(
                "Resource already exists",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleBadRequest_shouldReturn400() {

        BadRequestException ex =
                new BadRequestException("Invalid request");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleBadRequest(ex, request());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid request", response.getBody().getMessage());
    }

    @Test
    void handleUnauthorized_shouldReturn401() {

        UnauthorizedException ex =
                new UnauthorizedException("Unauthorized");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleUnauthorized(ex, request());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getMessage());
    }

    @Test
    void handleExternalApi_shouldReturn502() {

        ExternalApiException ex =
                new ExternalApiException("External API failed");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleExternalApi(ex, request());

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(502, response.getBody().getStatus());
        assertEquals(
                "External API failed",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleGeneric_shouldReturn500() {

        Exception ex =
                new RuntimeException("Something went wrong");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleGeneric(ex, request());

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(500, response.getBody().getStatus());
        assertEquals(
                "Something went wrong",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleRateLimit_shouldReturn429() {

        RateLimitExceededException ex =
                new RateLimitExceededException("Too many requests");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleRateLimit(ex, request());

        assertEquals(
                HttpStatus.TOO_MANY_REQUESTS,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(429, response.getBody().getStatus());
        assertEquals(
                "Too many requests",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleMissingRequestParameter_shouldReturn400() {

        var ex = new org.springframework.web.bind.MissingServletRequestParameterException(
                "keyword",
                "String"
        );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleMissingRequestParameter(ex, request());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(400, response.getBody().getStatus());
        assertEquals(
                "Required request parameter 'keyword' is missing",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleNoResourceFound_shouldReturn404() {

        NoResourceFoundException ex =
                new NoResourceFoundException(
                        org.springframework.http.HttpMethod.GET,
                        "/unknown"
                );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleNoResourceFound(ex, request());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(404, response.getBody().getStatus());
        assertEquals(
                "Resource not found",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleTypeMismatch_shouldReturn400() {

        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException(
                        "abc",
                        Long.class,
                        "id",
                        null,
                        new IllegalArgumentException()
                );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleTypeMismatch(ex, request());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        assertFalse(response.getBody().isSuccess());
        assertEquals(400, response.getBody().getStatus());
        assertEquals(
                "Invalid value for parameter 'id'",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleHttpMessageNotReadable_shouldReturn400() {

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("Invalid JSON");

        ResponseEntity<java.util.Map<String, String>> response =
                handler.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(
                "Required request body is missing or invalid JSON format",
                response.getBody().get("error")
        );
    }
}