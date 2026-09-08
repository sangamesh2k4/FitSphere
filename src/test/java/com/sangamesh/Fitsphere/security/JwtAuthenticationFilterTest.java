package com.sangamesh.Fitsphere.security;

import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                jwtService,
                customUserDetailsService
        );
    }
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noAuthorizationHeader_continuesFilterChain() throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    void authorizationHeaderWithoutBearer_continuesFilterChain() throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    void validToken_authenticatesUserAndContinuesChain() throws Exception {

        String token = "valid-token";
        String userId = "1";

        UserDetails userDetails = User
                .withUsername(userId)
                .password("password")
                .authorities(new SimpleGrantedAuthority("USER"))
                .build();

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.extractSubject(token))
                .thenReturn(userId);

        when(customUserDetailsService.loadUserByUsername(userId))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(token, userId))
                .thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        assertEquals(
                userDetails,
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
        );

        verify(jwtService).extractSubject(token);
        verify(customUserDetailsService).loadUserByUsername(userId);
        verify(jwtService).isTokenValid(token, userId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidToken_returnsUnauthorized() throws Exception {

        String token = "invalid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.extractSubject(token))
                .thenThrow(new JwtException("Invalid token"));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid access token"
        );

        verifyNoInteractions(customUserDetailsService);
        verifyNoInteractions(filterChain);
    }

    @Test
    void expiredToken_returnsUnauthorized() throws Exception {

        String token = "expired-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.extractSubject(token))
                .thenThrow(mock(ExpiredJwtException.class));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Access token expired"
        );

        verifyNoInteractions(customUserDetailsService);
        verifyNoInteractions(filterChain);
    }

    @Test
    void disabledUser_returnsUnauthorized() throws Exception {

        String token = "valid-token";
        String userId = "1";

        UserDetails disabledUser = User
                .withUsername(userId)
                .password("password")
                .authorities(new SimpleGrantedAuthority("USER"))
                .disabled(true)
                .build();

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.extractSubject(token))
                .thenReturn(userId);

        when(customUserDetailsService.loadUserByUsername(userId))
                .thenReturn(disabledUser);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Your account is suspended. Contact us for more details."
        );

        verifyNoInteractions(filterChain);

        verify(jwtService, never())
                .isTokenValid(anyString(), anyString());
    }

    @Test
    void invalidTokenArgument_returnsUnauthorized() throws Exception {

        String token = "bad-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.extractSubject(token))
                .thenThrow(new IllegalArgumentException("Bad token"));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid access token"
        );

        verifyNoInteractions(filterChain);
    }
    @Test
    void validTokenButAlreadyAuthenticated_doesNotReplaceAuthentication()
            throws Exception {

        String token = "valid-token";
        String userId = "1";

        UserDetails userDetails = User
                .withUsername(userId)
                .password("password")
                .authorities(new SimpleGrantedAuthority("USER"))
                .build();

        UsernamePasswordAuthenticationToken existingAuthentication =
                new UsernamePasswordAuthenticationToken(
                        "existing-user",
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.extractSubject(token))
                .thenReturn(userId);

        when(customUserDetailsService.loadUserByUsername(userId))
                .thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        assertSame(
                existingAuthentication,
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);

        verify(jwtService, never())
                .isTokenValid(anyString(), anyString());
    }
}