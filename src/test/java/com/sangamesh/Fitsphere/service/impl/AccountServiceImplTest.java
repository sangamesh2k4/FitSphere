package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.account.ChangeEmailRequestDto;
import com.sangamesh.Fitsphere.dto.account.CurrentUserResponseDto;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.exception.DuplicateResourceException;
import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.repository.UserRepository;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.RefreshTokenService;
import com.sangamesh.Fitsphere.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AccountServiceImpl accountService;


    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @AfterEach
    void clear() {
        // No shared stubbing here.
    }

    @Test
    void changeUsername_success() {

        User user = new User();
        user.setUsername("oldUsername");

        when(userService.getCurrentUser()).thenReturn(user);
        when(userRepository.existsByUsername("newUsername"))
                .thenReturn(false);

        accountService.changeUsername("newUsername");

        assertEquals("newUsername", user.getUsername());

        verify(userRepository).save(user);
    }

    @Test
    void changeUsername_throwsWhenUsernameExists() {

        User user = new User();
        user.setUsername("oldUsername");

        when(userService.getCurrentUser()).thenReturn(user);
        when(userRepository.existsByUsername("newUsername"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> accountService.changeUsername("newUsername")
        );

        assertEquals("oldUsername", user.getUsername());

        verify(userRepository, never()).save(any());
    }

    @Test
    void checkUsernameAvailability_whenAvailable() {

        when(userRepository.existsByUsername("john"))
                .thenReturn(false);

        ChangeEmailRequestDto.UsernameAvailabilityResponseDto response =
                accountService.checkUsernameAvailability("john");

        assertTrue(response.isAvailable());

        verify(userRepository)
                .existsByUsername("john");
    }

    @Test
    void checkUsernameAvailability_whenUnavailable() {

        when(userRepository.existsByUsername("john"))
                .thenReturn(true);

        ChangeEmailRequestDto.UsernameAvailabilityResponseDto response =
                accountService.checkUsernameAvailability("john");

        assertFalse(response.isAvailable());

        verify(userRepository)
                .existsByUsername("john");
    }

    @Test
    void changePassword_success() {

        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");

        when(userService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(
                "oldPassword",
                "encodedOldPassword"))
                .thenReturn(true);

        when(passwordEncoder.matches(
                "newPassword",
                "encodedOldPassword"))
                .thenReturn(false);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedNewPassword");

        accountService.changePassword(
                "oldPassword",
                "newPassword");

        assertEquals(
                "encodedNewPassword",
                user.getPassword());

        verify(userRepository).save(user);
        verify(refreshTokenService)
                .deleteAllUserTokens(1L);
    }

    @Test
    void changePassword_throwsWhenCurrentPasswordIncorrect() {

        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");

        when(userService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedOldPassword"))
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> accountService.changePassword(
                        "wrongPassword",
                        "newPassword")
        );

        verify(userRepository, never()).save(any());
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void changePassword_throwsWhenNewPasswordSameAsCurrent() {

        User user = new User();
        user.setId(1L);
        user.setPassword("encodedPassword");

        when(userService.getCurrentUser()).thenReturn(user);

        when(passwordEncoder.matches(
                "currentPassword",
                "encodedPassword"))
                .thenReturn(true);

        when(passwordEncoder.matches(
                "currentPassword",
                "encodedPassword"))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> accountService.changePassword(
                        "currentPassword",
                        "currentPassword")
        );

        verify(userRepository, never()).save(any());
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void sendEmailChangeOtp_success() {

        User user = new User();
        user.setId(1L);

        when(userService.getCurrentUser()).thenReturn(user);
        when(userRepository.existsByEmail("new@gmail.com"))
                .thenReturn(false);

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        accountService.sendEmailChangeOtp("new@gmail.com");

        verify(valueOperations).set(
                eq("email-change:1:new@gmail.com"),
                anyString(),
                any()
        );

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmailChangeOtp_throwsWhenEmailExists() {

        User user = new User();
        user.setId(1L);

        when(userService.getCurrentUser()).thenReturn(user);
        when(userRepository.existsByEmail("existing@gmail.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> accountService.sendEmailChangeOtp(
                        "existing@gmail.com")
        );

        verifyNoInteractions(redisTemplate);
        verifyNoInteractions(mailSender);
    }

    @Test
    void verifyAndChangeEmail_success() {

        User user = new User();
        user.setId(1L);
        user.setEmail("old@gmail.com");

        when(userService.getCurrentUser()).thenReturn(user);

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(
                "email-change:1:new@gmail.com"))
                .thenReturn("123456");

        when(userRepository.existsByEmail("new@gmail.com"))
                .thenReturn(false);

        accountService.verifyAndChangeEmail(
                "new@gmail.com",
                "123456");

        assertEquals(
                "new@gmail.com",
                user.getEmail());

        verify(userRepository).save(user);

        verify(refreshTokenService)
                .deleteAllUserTokens(1L);

        verify(redisTemplate)
                .delete("email-change:1:new@gmail.com");
    }

    @Test
    void verifyAndChangeEmail_throwsWhenOtpMissing() {

        User user = new User();
        user.setId(1L);

        when(userService.getCurrentUser()).thenReturn(user);

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(
                "email-change:1:new@gmail.com"))
                .thenReturn(null);

        assertThrows(
                BadRequestException.class,
                () -> accountService.verifyAndChangeEmail(
                        "new@gmail.com",
                        "123456")
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void verifyAndChangeEmail_throwsWhenOtpInvalid() {

        User user = new User();
        user.setId(1L);

        when(userService.getCurrentUser()).thenReturn(user);

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(
                "email-change:1:new@gmail.com"))
                .thenReturn("654321");

        assertThrows(
                BadRequestException.class,
                () -> accountService.verifyAndChangeEmail(
                        "new@gmail.com",
                        "123456")
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void verifyAndChangeEmail_throwsWhenEmailAlreadyExists() {

        User user = new User();
        user.setId(1L);

        when(userService.getCurrentUser()).thenReturn(user);

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(
                "email-change:1:new@gmail.com"))
                .thenReturn("123456");

        when(userRepository.existsByEmail("new@gmail.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> accountService.verifyAndChangeEmail(
                        "new@gmail.com",
                        "123456")
        );

        verify(userRepository, never()).save(any());
        verify(refreshTokenService, never())
                .deleteAllUserTokens(anyLong());
        verify(redisTemplate, never())
                .delete(anyString());
    }

    @Test
    void getCurrentUser_success() {

        User user = new User();
        user.setUsername("john");
        user.setEmail("john@gmail.com");

        when(userService.getCurrentUser())
                .thenReturn(user);

        CurrentUserResponseDto response =
                accountService.getCurrentUser();

        assertEquals("john", response.getUsername());
        assertEquals("john@gmail.com", response.getEmail());

        verify(userService).getCurrentUser();
    }
}