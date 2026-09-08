package com.sangamesh.Fitsphere.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ApiErrorResponse {


    private boolean success;

    private int status;

    private String message;

    private String path;

    private LocalDateTime timestamp;

    private Map<String, String> errors;
}