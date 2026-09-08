package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    // =========================
    // SEND RESET OTP
    // =========================

    @Test
    void sendResetOtp_success() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(true);

        passwordResetService.sendResetOtp("john@gmail.com");

        verify(valueOperations).set(
                eq("password-reset-otp:john@gmail.com"),
                argThat(otp -> otp.matches("\\d{6}")),
                eq(Duration.ofMinutes(10))
        );

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals("john@gmail.com", message.getTo()[0]);
        assertEquals(
                "FitSphere Password Reset",
                message.getSubject()
        );
        assertNotNull(message.getText());
        assertTrue(message.getText().contains(
                "Your FitSphere password reset OTP is:"
        ));
    }

    @Test
    void sendResetOtp_throwsWhenUserDoesNotExist() {

        when(userRepository.existsByEmail("unknown@gmail.com"))
                .thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> passwordResetService.sendResetOtp(
                        "unknown@gmail.com"
                )
        );

        assertEquals(
                "User not found with this email",
                exception.getMessage()
        );

        verify(redisTemplate, never()).opsForValue();
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    // =========================
    // VERIFY RESET OTP
    // =========================

    @Test
    void verifyResetOtp_success() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(
                "password-reset-otp:john@gmail.com"
        )).thenReturn("123456");

        String resetToken = passwordResetService.verifyResetOtp(
                "john@gmail.com",
                "123456"
        );

        assertNotNull(resetToken);
        assertFalse(resetToken.isBlank());

        verify(valueOperations).set(
                eq("password-reset-token:" + resetToken),
                eq("john@gmail.com"),
                eq(Duration.ofMinutes(10))
        );

        verify(redisTemplate)
                .delete("password-reset-otp:john@gmail.com");
    }

    @Test
    void verifyResetOtp_throwsWhenOtpNotFound() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(
                "password-reset-otp:john@gmail.com"
        )).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> passwordResetService.verifyResetOtp(
                        "john@gmail.com",
                        "123456"
                )
        );

        assertEquals(
                "OTP expired or not found",
                exception.getMessage()
        );

        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void verifyResetOtp_throwsWhenOtpInvalid() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(
                "password-reset-otp:john@gmail.com"
        )).thenReturn("999999");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> passwordResetService.verifyResetOtp(
                        "john@gmail.com",
                        "123456"
                )
        );

        assertEquals(
                "Invalid OTP",
                exception.getMessage()
        );

        verify(redisTemplate, never()).delete(anyString());

        verify(valueOperations, never()).set(
                anyString(),
                anyString(),
                any(Duration.class)
        );
    }

    // =========================
    // RESET PASSWORD
    // =========================

    @Test
    void resetPassword_success() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(
                "password-reset-token:reset-token"
        )).thenReturn("john@gmail.com");

        User user = new User();
        user.setId(1L);
        user.setEmail("john@gmail.com");
        user.setPassword("oldPassword");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newPassword123"))
                .thenReturn("encodedNewPassword");

        passwordResetService.resetPassword(
                "reset-token",
                "newPassword123"
        );

        assertEquals(
                "encodedNewPassword",
                user.getPassword()
        );

        verify(passwordEncoder)
                .encode("newPassword123");

        verify(userRepository).save(user);

        verify(redisTemplate)
                .delete("password-reset-token:reset-token");
    }

    @Test
    void resetPassword_throwsWhenResetTokenInvalid() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(
                "password-reset-token:invalid-token"
        )).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> passwordResetService.resetPassword(
                        "invalid-token",
                        "newPassword123"
                )
        );

        assertEquals(
                "Reset token expired or invalid",
                exception.getMessage()
        );

        verify(userRepository, never())
                .findByEmail(anyString());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(redisTemplate, never())
                .delete(anyString());
    }

    @Test
    void resetPassword_throwsWhenUserNotFound() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(
                "password-reset-token:reset-token"
        )).thenReturn("john@gmail.com");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> passwordResetService.resetPassword(
                        "reset-token",
                        "newPassword123"
                )
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any());

        verify(redisTemplate, never())
                .delete(anyString());
    }
}