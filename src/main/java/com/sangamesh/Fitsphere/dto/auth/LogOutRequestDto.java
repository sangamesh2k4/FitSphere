package com.sangamesh.Fitsphere.dto.auth;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogOutRequestDto {

    private String RefreshToken;
}
