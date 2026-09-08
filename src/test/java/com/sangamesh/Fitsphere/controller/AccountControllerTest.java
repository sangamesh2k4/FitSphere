package com.sangamesh.Fitsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.account.*;
import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    void changeUsername_success() throws Exception {

        ChangeUsernameRequestDto request =
                new ChangeUsernameRequestDto();

        request.setNewUsername("newUsername");

        doNothing().when(accountService)
                .changeUsername("newUsername");

        mockMvc.perform(patch("/api/account/username")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Username changed successfully"));

        verify(accountService)
                .changeUsername("newUsername");
    }

    @Test
    void checkUsername_success() throws Exception {

        ChangeEmailRequestDto.UsernameAvailabilityResponseDto response =
                new ChangeEmailRequestDto.UsernameAvailabilityResponseDto();

        response.setAvailable(true);

        when(accountService.checkUsernameAvailability("john"))
                .thenReturn(response);

        mockMvc.perform(get("/api/account/username/check")
                        .param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));

        verify(accountService)
                .checkUsernameAvailability("john");
    }

    @Test
    void checkUsername_missingUsername_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/account/username/check"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void changePassword_success() throws Exception {

        ChangePasswordRequestDto request =
                new ChangePasswordRequestDto();

        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");

        doNothing().when(accountService)
                .changePassword("oldPassword", "newPassword");

        mockMvc.perform(patch("/api/account/password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Password changed successfully"));

        verify(accountService)
                .changePassword("oldPassword", "newPassword");
    }

    @Test
    void sendEmailChangeOtp_success() throws Exception {

        ChangeEmailRequestDto request =
                new ChangeEmailRequestDto();

        request.setNewEmail("new@gmail.com");

        doNothing().when(accountService)
                .sendEmailChangeOtp("new@gmail.com");

        mockMvc.perform(post("/api/account/email/otp")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "OTP sent to new email"));

        verify(accountService)
                .sendEmailChangeOtp("new@gmail.com");
    }

    @Test
    void changeEmail_success() throws Exception {

        VerifyEmailChangeRequestDto request =
                new VerifyEmailChangeRequestDto();

        request.setNewEmail("new@gmail.com");
        request.setOtp("123456");

        doNothing().when(accountService)
                .verifyAndChangeEmail("new@gmail.com", "123456");

        mockMvc.perform(patch("/api/account/email")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Email changed successfully"));

        verify(accountService)
                .verifyAndChangeEmail(
                        "new@gmail.com",
                        "123456");
    }

    @Test
    void getCurrentUser_success() throws Exception {

        CurrentUserResponseDto response =
                new CurrentUserResponseDto();

        when(accountService.getCurrentUser())
                .thenReturn(response);

        mockMvc.perform(get("/api/account/me"))
                .andExpect(status().isOk());

        verify(accountService)
                .getCurrentUser();
    }

    @Test
    void changeUsername_invalidJson_returnsBadRequest()
            throws Exception {

        mockMvc.perform(patch("/api/account/username")
                        .contentType("application/json")
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void changePassword_invalidJson_returnsBadRequest()
            throws Exception {

        mockMvc.perform(patch("/api/account/password")
                        .contentType("application/json")
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void sendEmailChangeOtp_invalidJson_returnsBadRequest()
            throws Exception {

        mockMvc.perform(post("/api/account/email/otp")
                        .contentType("application/json")
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void changeEmail_invalidJson_returnsBadRequest()
            throws Exception {

        mockMvc.perform(patch("/api/account/email")
                        .contentType("application/json")
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }
}