package com.sangamesh.Fitsphere.security;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret =
            "this-is-a-test-secret-key-that-is-long-enough-for-hmac";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                secret
        );

        ReflectionTestUtils.setField(
                jwtService,
                "accessExpiration",
                60_000L
        );

        ReflectionTestUtils.setField(
                jwtService,
                "refreshExpiration",
                120_000L
        );
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ROLE_USER);
        return user;
    }

    @Test
    void generateAccessToken_containsCorrectClaims() {

        User user = createUser();

        String token = jwtService.generateAccessToken(user);

        assertNotNull(token);

        assertEquals(
                "1",
                jwtService.extractSubject(token)
        );

        assertEquals(
                TokenType.ACCESS.name(),
                jwtService.extractTokenType(token)
        );

        assertEquals(
                Role.ROLE_USER.name(),
                jwtService.extractRole(token)
        );
    }

    @Test
    void generateRefreshToken_containsCorrectClaims() {

        User user = createUser();

        String token = jwtService.generateRefreshToken(user);

        assertNotNull(token);

        assertEquals(
                "1",
                jwtService.extractSubject(token)
        );

        assertEquals(
                TokenType.REFRESH.name(),
                jwtService.extractTokenType(token)
        );

        assertEquals(
                Role.ROLE_USER.name(),
                jwtService.extractRole(token)
        );
    }

    @Test
    void extractSubject_returnsUserId() {

        User user = createUser();

        String token = jwtService.generateAccessToken(user);

        assertEquals(
                "1",
                jwtService.extractSubject(token)
        );
    }

    @Test
    void extractRole_returnsUserRole() {

        User user = createUser();

        String token = jwtService.generateAccessToken(user);

        assertEquals(
                "ROLE_USER",
                jwtService.extractRole(token)
        );
    }

    @Test
    void extractTokenType_returnsCorrectType() {

        User user = createUser();

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);

        assertEquals(
                TokenType.ACCESS.name(),
                jwtService.extractTokenType(accessToken)
        );

        assertEquals(
                TokenType.REFRESH.name(),
                jwtService.extractTokenType(refreshToken)
        );
    }

    @Test
    void extractExpiration_returnsFutureDateForValidToken() {

        User user = createUser();

        String token = jwtService.generateAccessToken(user);

        Date expiration =
                jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenExpired_returnsFalseForValidToken() {

        User user = createUser();

        String token = jwtService.generateAccessToken(user);

        assertFalse(
                jwtService.isTokenExpired(token)
        );
    }

    @Test
    void hasTokenType_returnsCorrectResult() {

        User user = createUser();

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);

        assertTrue(
                jwtService.hasTokenType(
                        accessToken,
                        TokenType.ACCESS
                )
        );

        assertFalse(
                jwtService.hasTokenType(
                        accessToken,
                        TokenType.REFRESH
                )
        );

        assertTrue(
                jwtService.hasTokenType(
                        refreshToken,
                        TokenType.REFRESH
                )
        );
    }

    @Test
    void isTokenValid_returnsTrueForValidAccessToken() {

        User user = createUser();

        String token =
                jwtService.generateAccessToken(user);

        assertTrue(
                jwtService.isTokenValid(token, "1")
        );
    }

    @Test
    void isTokenValid_returnsFalseForDifferentUser() {

        User user = createUser();

        String token =
                jwtService.generateAccessToken(user);

        assertFalse(
                jwtService.isTokenValid(token, "999")
        );
    }

    @Test
    void isTokenValid_returnsFalseForRefreshToken() {

        User user = createUser();

        String token =
                jwtService.generateRefreshToken(user);

        assertFalse(
                jwtService.isTokenValid(token, "1")
        );
    }

    @Test
    void getRefreshTokenExpiry_returnsFutureDateTime() {

        LocalDateTime before = LocalDateTime.now();

        LocalDateTime expiry =
                jwtService.getRefreshTokenExpiry();

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(expiry);
        assertTrue(expiry.isAfter(before));
        assertTrue(expiry.isBefore(after.plusMinutes(3)));
    }

    @Test
    void invalidToken_throwsException() {

        assertThrows(
                Exception.class,
                () -> jwtService.extractSubject("invalid-token")
        );
    }
}