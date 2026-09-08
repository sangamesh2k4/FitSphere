package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.entity.RefreshToken;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("john");
    }

    // =========================
    // SAVE
    // =========================

    @Test
    void saveRefreshToken_success() {

        String token = "refresh-token";
        LocalDateTime expiry = LocalDateTime.now().plusDays(7);

        RefreshToken savedToken = new RefreshToken();
        savedToken.setToken(token);
        savedToken.setUser(user);
        savedToken.setExpiryDate(expiry);

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(savedToken);

        RefreshToken result =
                refreshTokenService.saveRefreshToken(token, user, expiry);

        assertNotNull(result);
        assertEquals(token, result.getToken());
        assertEquals(user, result.getUser());
        assertEquals(expiry, result.getExpiryDate());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    // =========================
    // FIND
    // =========================

    @Test
    void findByToken_success() {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result =
                refreshTokenService.findByToken("refresh-token");

        assertSame(refreshToken, result);

        verify(refreshTokenRepository)
                .findByToken("refresh-token");
    }

    @Test
    void findByToken_throwsExceptionWhenTokenNotFound() {

        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> refreshTokenService.findByToken("invalid-token")
        );

        assertEquals("Refresh token not found", exception.getMessage());
    }

    // =========================
    // VALIDATE
    // =========================

    @Test
    void validateRefreshToken_successWhenTokenNotExpired() {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(10)
        );

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result =
                refreshTokenService.validateRefreshToken("refresh-token");

        assertSame(refreshToken, result);
    }

    @Test
    void validateRefreshToken_throwsExceptionWhenTokenExpired() {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        refreshToken.setExpiryDate(
                LocalDateTime.now().minusMinutes(1)
        );

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> refreshTokenService.validateRefreshToken("refresh-token")
        );

        assertEquals("Refresh token expired", exception.getMessage());
    }

    @Test
    void validateRefreshToken_throwsExceptionWhenTokenNotFound() {

        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> refreshTokenService.validateRefreshToken("invalid-token")
        );

        assertEquals("Refresh token not found", exception.getMessage());
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void deleteRefreshToken_success() {

        refreshTokenService.deleteRefreshToken("refresh-token");

        verify(refreshTokenRepository)
                .deleteByToken("refresh-token");
    }

    @Test
    void deleteAllUserTokens_success() {

        refreshTokenService.deleteAllUserTokens(1L);

        verify(refreshTokenRepository)
                .deleteByUser_Id(1L);
    }
}