package com.sangamesh.Fitsphere.dto.auth;

import lombok.Data;

@Data
public class ResetPasswordRequestDto {

    private String resetToken;
    private String newPassword;
}