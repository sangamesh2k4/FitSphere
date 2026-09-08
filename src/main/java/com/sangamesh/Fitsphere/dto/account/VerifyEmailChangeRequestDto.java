package com.sangamesh.Fitsphere.dto.account;

import lombok.Data;

@Data
public class VerifyEmailChangeRequestDto {

    private String newEmail;

    private String otp;
}