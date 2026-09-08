package com.sangamesh.Fitsphere.dto.auth;


import lombok.Data;

@Data
public class LoginRequest {
    public String usernameOrEmail;
    public String password;
}
