package com.sangamesh.Fitsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.auth.*;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Role;
import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.AuthService;
import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import com.sangamesh.Fitsphere.service.EmailVerificationService;
import com.sangamesh.Fitsphere.service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    // =========================
    // REGISTER
    // =========================

    @Test
    void register_success() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@gmail.com");
        request.setPassword("password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn("Verification OTP sent to email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Verification OTP sent to email"));

        verify(authService).register(any(RegisterRequest.class));
    }

    // =========================
    // LOGIN
    // =========================

    @Test
    void login_success() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("john");
        request.setPassword("password123");

        AuthResponseDto response =
                new AuthResponseDto(
                        "access-token",
                        "refresh-token",
                        Role.ROLE_USER
                );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        verify(authService).login(any(LoginRequest.class));
    }

    // =========================
    // JWT TEST ENDPOINTS
    // =========================

    @Test
    void testToken_success() throws Exception {

        when(jwtService.extractTokenType("token"))
                .thenReturn("ACCESS");

        mockMvc.perform(get("/api/auth/test")
                        .param("token", "token"))
                .andExpect(status().isOk())
                .andExpect(content().string("ACCESS"));

        verify(jwtService).extractTokenType("token");
    }

    @Test
    void testAccess_success() throws Exception {

        when(jwtService.extractTokenType("token"))
                .thenReturn("ACCESS");

        mockMvc.perform(get("/api/auth/test/access")
                        .param("token", "token"))
                .andExpect(status().isOk())
                .andExpect(content().string("ACCESS"));

        verify(jwtService).extractTokenType("token");
    }

    @Test
    void testSubject_success() throws Exception {

        when(jwtService.extractSubject("token"))
                .thenReturn("1");

        mockMvc.perform(get("/api/auth/test/subject")
                        .param("token", "token"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(jwtService).extractSubject("token");
    }

    @Test
    void testExpiry_success() throws Exception {

        Date expiry = new Date(2000000000000L);

        when(jwtService.extractExpiration("token"))
                .thenReturn(expiry);

        mockMvc.perform(get("/api/auth/test/expiry")
                        .param("token", "token"))
                .andExpect(status().isOk());

        verify(jwtService).extractExpiration("token");
    }

    // =========================
    // REFRESH
    // =========================

    @Test
    void refresh_success() throws Exception {

        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("refresh-token");

        AccessTokenResponseDto response =
                new AccessTokenResponseDto("new-access-token");

        when(authService.refreshAccessToken(
                any(RefreshTokenRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("new-access-token"));

        verify(authService)
                .refreshAccessToken(any(RefreshTokenRequestDto.class));
    }

    // =========================
    // LOGOUT
    // =========================

    @Test
    void logout_success() throws Exception {

        LogOutRequestDto request = new LogOutRequestDto();
        request.setRefreshToken("refresh-token");

        doNothing().when(authService)
                .Logout(any(LogOutRequestDto.class));

        mockMvc.perform(post("/api/auth/logout")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        verify(authService)
                .Logout(any(LogOutRequestDto.class));
    }

    // =========================
    // VERIFY EMAIL
    // =========================
    @Test
    void verifyEmail_success() throws Exception {

        VerifyEmailRequestDto request = new VerifyEmailRequestDto();
        request.setEmail("john@gmail.com");
        request.setOtp("123456");

        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@gmail.com");

        when(emailVerificationService.verifyRegistration(
                "john@gmail.com",
                "123456"
        )).thenReturn(user);

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Email verified and account created successfully"));

        verify(emailVerificationService)
                .verifyRegistration("john@gmail.com", "123456");
    }

    // =========================
    // FORGOT PASSWORD
    // =========================

    @Test
    void forgotPassword_success() throws Exception {

        ForgotPasswordRequestDto request =
                new ForgotPasswordRequestDto();

        request.setEmail("john@gmail.com");

        doNothing().when(passwordResetService)
                .sendResetOtp("john@gmail.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Password reset OTP sent successfully"));

        verify(passwordResetService)
                .sendResetOtp("john@gmail.com");
    }

    // =========================
    // VERIFY RESET OTP
    // =========================

    @Test
    void verifyResetOtp_success() throws Exception {

        VerifyResetOtpRequestDto request =
                new VerifyResetOtpRequestDto();

        request.setEmail("john@gmail.com");
        request.setOtp("123456");

        when(passwordResetService.verifyResetOtp(
                "john@gmail.com",
                "123456"))
                .thenReturn("reset-token");

        mockMvc.perform(post("/api/auth/verify-reset-otp")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("reset-token"));

        verify(passwordResetService)
                .verifyResetOtp("john@gmail.com", "123456");
    }

    // =========================
    // RESET PASSWORD
    // =========================

    @Test
    void resetPassword_success() throws Exception {

        ResetPasswordRequestDto request =
                new ResetPasswordRequestDto();

        request.setResetToken("reset-token");
        request.setNewPassword("newPassword123");

        doNothing().when(passwordResetService)
                .resetPassword("reset-token", "newPassword123");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Password reset successfully"));

        verify(passwordResetService)
                .resetPassword("reset-token", "newPassword123");
    }

    // =========================
    // RESEND VERIFICATION
    // =========================

    @Test
    void resendVerification_success() throws Exception {

        EmailRequestDto request = new EmailRequestDto();
        request.setEmail("john@gmail.com");

        doNothing().when(emailVerificationService)
                .resendVerification("john@gmail.com");

        mockMvc.perform(post("/api/auth/resend-verification")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Verification OTP sent successfully"));

        verify(emailVerificationService)
                .resendVerification("john@gmail.com");
    }
}