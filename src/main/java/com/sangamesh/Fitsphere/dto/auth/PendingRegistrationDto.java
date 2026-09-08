package com.sangamesh.Fitsphere.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingRegistrationDto {

    private String username;
    private String email;
    private String encodedPassword;
}
