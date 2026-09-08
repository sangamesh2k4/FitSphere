package com.sangamesh.Fitsphere.dto.auth;

import lombok.Data;

@Data
public class VerifyResetOtpRequestDto {

    private String email;
    private String otp;
}