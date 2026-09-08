package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.EmailService;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private UsageTrackingService usageTrackingService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    void sendOtp_success() throws Exception {

        doNothing().when(emailService)
                .sendVerificationOtp("john@gmail.com");

        mockMvc.perform(post("/api/auth/otp/send")
                        .param("email", "john@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "OTP sent successfully"
                ));

        verify(emailService)
                .sendVerificationOtp("john@gmail.com");
    }

    @Test
    void verifyOtp_success() throws Exception {

        doNothing().when(emailService)
                .verifyEmailOtp("john@gmail.com", "123456");

        mockMvc.perform(post("/api/auth/otp/verify")
                        .param("email", "john@gmail.com")
                        .param("otp", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "OTP verified successfully"
                ));

        verify(emailService)
                .verifyEmailOtp("john@gmail.com", "123456");
    }

    @Test
    void sendOtp_missingEmail_returnsBadRequest() throws Exception {

        mockMvc.perform(post("/api/auth/otp/send"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyOtp_missingEmail_returnsBadRequest() throws Exception {

        mockMvc.perform(post("/api/auth/otp/verify")
                        .param("otp", "123456"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyOtp_missingOtp_returnsBadRequest() throws Exception {

        mockMvc.perform(post("/api/auth/otp/verify")
                        .param("email", "john@gmail.com"))
                .andExpect(status().isBadRequest());
    }
}
