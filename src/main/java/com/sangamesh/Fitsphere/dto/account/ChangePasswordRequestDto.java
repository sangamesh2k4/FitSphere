package com.sangamesh.Fitsphere.dto.account;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {

    private String currentPassword;
    private String newPassword;
}
