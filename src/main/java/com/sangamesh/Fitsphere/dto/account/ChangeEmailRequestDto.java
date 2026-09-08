package com.sangamesh.Fitsphere.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ChangeEmailRequestDto {

    private String newEmail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsernameAvailabilityResponseDto {

        private boolean available;
    }
}
