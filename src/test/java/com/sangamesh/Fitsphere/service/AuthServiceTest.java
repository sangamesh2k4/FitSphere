package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.auth.*;
import com.sangamesh.Fitsphere.entity.RefreshToken;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Role;
import com.sangamesh.Fitsphere.exception.DuplicateResourceException;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.security.TokenType;
import com.sangamesh.Fitsphere.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
    }

    // =========================
    // REGISTER
    // =========================

    @Test
    void register_success() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        String result = authService.register(request);

        assertEquals("Verification OTP sent to email", result);

        verify(userRepository).existsByUsername("john");
        verify(userRepository).existsByEmail("john@gmail.com");
        verify(passwordEncoder).encode("password123");
        verify(emailVerificationService).startVerification(any(PendingRegistrationDto.class));
    }

    @Test
    void register_throwsExceptionWhenUsernameAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(request)
        );

        verify(userRepository).existsByUsername("john");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(emailVerificationService, never())
                .startVerification(any(PendingRegistrationDto.class));
    }

    @Test
    void register_throwsExceptionWhenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(request)
        );

        verify(userRepository).existsByUsername("john");
        verify(userRepository).existsByEmail("john@gmail.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(emailVerificationService, never())
                .startVerification(any(PendingRegistrationDto.class));
    }

    @Test
    void register_sendsEncodedPasswordToVerificationService() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        authService.register(request);

        var captor =
                org.mockito.ArgumentCaptor.forClass(PendingRegistrationDto.class);

        verify(emailVerificationService).startVerification(captor.capture());

        PendingRegistrationDto pending = captor.getValue();

        assertEquals("john", pending.getUsername());
        assertEquals("john@gmail.com", pending.getEmail());
        assertEquals("encodedPassword", pending.getEncodedPassword());
    }

    // =========================
    // LOGIN
    // =========================

    @Test
    void login_success() {

        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("john");
        request.setPassword("password123");

        when(userRepository.findByUsernameOrEmail("john", "john"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateAccessToken(user))
                .thenReturn("access-token");

        when(jwtService.generateRefreshToken(user))
                .thenReturn("refresh-token");

        LocalDateTime expiry = LocalDateTime.now().plusDays(7);

        when(jwtService.getRefreshTokenExpiry())
                .thenReturn(expiry);

        AuthResponseDto response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(Role.ROLE_USER, response.getRole());

        verify(refreshTokenService)
                .saveRefreshToken("refresh-token", user, expiry);
    }

    @Test
    void login_throwsExceptionWhenUserNotFound() {

        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("unknown");
        request.setPassword("password123");

        when(userRepository.findByUsernameOrEmail("unknown", "unknown"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid username or Email", exception.getMessage());

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    void login_throwsExceptionWhenUserIsSuspended() {

        user.setEnabled(false);

        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("john");
        request.setPassword("password123");

        when(userRepository.findByUsernameOrEmail("john", "john"))
                .thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        assertEquals("your account is suspended", exception.getMessage());

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    void login_throwsExceptionWhenPasswordIsInvalid() {

        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("john");
        request.setPassword("wrongPassword");

        when(userRepository.findByUsernameOrEmail("john", "john"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid password", exception.getMessage());

        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
        verify(refreshTokenService, never())
                .saveRefreshToken(anyString(), any(), any());
    }

    // =========================
    // DELETE ACCOUNT
    // =========================

    @Test
    void deleteAccount_success() {

        when(userRepository.existsById(1L)).thenReturn(true);

        authService.deleteAccount(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteAccount_throwsExceptionWhenUserDoesNotExist() {

        when(userRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.deleteAccount(1L)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, never()).deleteById(anyLong());
    }

    // =========================
    // REFRESH ACCESS TOKEN
    // =========================

    @Test
    void refreshAccessToken_success() {

        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("refresh-token");

        RefreshToken refreshToken = mock(RefreshToken.class);

        when(refreshTokenService.validateRefreshToken("refresh-token"))
                .thenReturn(refreshToken);

        when(refreshToken.getToken())
                .thenReturn("refresh-token");

        when(jwtService.hasTokenType(
                "refresh-token",
                TokenType.REFRESH
        )).thenReturn(true);

        when(jwtService.extractSubject("refresh-token"))
                .thenReturn("1");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(jwtService.generateAccessToken(user))
                .thenReturn("new-access-token");

        AccessTokenResponseDto response =
                authService.refreshAccessToken(request);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());

        verify(jwtService).generateAccessToken(user);
    }

    @Test
    void refreshAccessToken_throwsExceptionForInvalidTokenType() {

        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("access-token");

        RefreshToken refreshToken = mock(RefreshToken.class);

        when(refreshTokenService.validateRefreshToken("access-token"))
                .thenReturn(refreshToken);

        when(refreshToken.getToken())
                .thenReturn("access-token");

        when(jwtService.hasTokenType(
                "access-token",
                TokenType.REFRESH
        )).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.refreshAccessToken(request)
        );

        assertEquals("Invalid Refresh token", exception.getMessage());

        verify(jwtService, never()).extractSubject(anyString());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void refreshAccessToken_throwsExceptionWhenUserNotFound() {

        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("refresh-token");

        RefreshToken refreshToken = mock(RefreshToken.class);

        when(refreshTokenService.validateRefreshToken("refresh-token"))
                .thenReturn(refreshToken);

        when(refreshToken.getToken())
                .thenReturn("refresh-token");

        when(jwtService.hasTokenType(
                "refresh-token",
                TokenType.REFRESH
        )).thenReturn(true);

        when(jwtService.extractSubject("refresh-token"))
                .thenReturn("999");

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.refreshAccessToken(request)
        );

        assertEquals("user not found", exception.getMessage());

        verify(jwtService, never()).generateAccessToken(any());
    }

    // =========================
    // LOGOUT
    // =========================

    @Test
    void logout_success() {

        LogOutRequestDto request = new LogOutRequestDto();
        request.setRefreshToken("refresh-token");

        authService.Logout(request);

        verify(refreshTokenService)
                .deleteRefreshToken("refresh-token");
    }
}