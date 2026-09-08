package com.sangamesh.Fitsphere.dto.account;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserResponseDto {
    private String username;
    private String email;
}
