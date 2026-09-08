package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Role;
import com.sangamesh.Fitsphere.repository.UserRepository;
import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded-password");
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
    }

    @Test
    void loadUserByUsername_returnsUserDetails() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDetails result =
                customUserDetailsService.loadUserByUsername("1");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encoded-password", result.getPassword());
        assertTrue(result.isEnabled());

        assertTrue(
                result.getAuthorities()
                        .contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );

        verify(userRepository).findById(1L);
    }

    @Test
    void loadUserByUsername_returnsDisabledUserDetailsForDisabledUser() {

        user.setEnabled(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDetails result =
                customUserDetailsService.loadUserByUsername("1");

        assertNotNull(result);
        assertFalse(result.isEnabled());

        verify(userRepository).findById(1L);
    }

    @Test
    void loadUserByUsername_throwsExceptionWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> customUserDetailsService.loadUserByUsername("1")
                );

        assertEquals("user not found", exception.getMessage());

        verify(userRepository).findById(1L);
    }

    @Test
    void loadUserByUsername_parsesUserIdCorrectly() {

        when(userRepository.findById(123L))
                .thenReturn(Optional.of(user));

        customUserDetailsService.loadUserByUsername("123");

        verify(userRepository).findById(123L);
    }

    @Test
    void loadUserByUsername_throwsExceptionForInvalidUserId() {

        assertThrows(
                NumberFormatException.class,
                () -> customUserDetailsService.loadUserByUsername("abc")
        );

        verifyNoInteractions(userRepository);
    }
}