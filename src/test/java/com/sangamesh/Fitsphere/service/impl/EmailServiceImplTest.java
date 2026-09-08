package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // =========================
    // SEND VERIFICATION OTP
    // =========================

    @Test
    void sendVerificationOtp_success() {

        emailService.sendVerificationOtp("john@gmail.com");

        verify(valueOperations).set(
                eq("email-verification:john@gmail.com"),
                argThat(otp -> otp.matches("\\d{6}")),
                eq(Duration.ofMinutes(10))
        );

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals("john@gmail.com", message.getTo()[0]);
        assertEquals(
                "FitSphere Email Verification",
                message.getSubject()
        );
        assertTrue(message.getText().contains(
                "Your FitSphere verification OTP is:"
        ));
    }

    // =========================
    // VERIFY EMAIL OTP
    // =========================

    @Test
    void verifyEmailOtp_success() {

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(false);

        when(valueOperations.get(
                "email-verification:john@gmail.com"
        )).thenReturn("123456");

        emailService.verifyEmailOtp(
                "john@gmail.com",
                "123456"
        );

        verify(redisTemplate)
                .delete("email-verification:john@gmail.com");
    }

    @Test
    void verifyEmailOtp_throwsWhenEmailAlreadyRegistered() {

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailService.verifyEmailOtp(
                        "john@gmail.com",
                        "123456"
                )
        );

        assertEquals(
                "Email is already registered",
                exception.getMessage()
        );

        verify(redisTemplate, never()).opsForValue();
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void verifyEmailOtp_throwsWhenOtpNotFound() {

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(false);

        when(valueOperations.get(
                "email-verification:john@gmail.com"
        )).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailService.verifyEmailOtp(
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
    void verifyEmailOtp_throwsWhenOtpInvalid() {

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(false);

        when(valueOperations.get(
                "email-verification:john@gmail.com"
        )).thenReturn("999999");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailService.verifyEmailOtp(
                        "john@gmail.com",
                        "123456"
                )
        );

        assertEquals(
                "Invalid OTP",
                exception.getMessage()
        );

        verify(redisTemplate, never()).delete(anyString());
    }

    // =========================
    // PASSWORD RESET OTP
    // =========================

    @Test
    void sendPasswordResetOtp_success() {

        emailService.sendPasswordResetOtp("john@gmail.com");

        verify(valueOperations).set(
                eq("password-reset:john@gmail.com"),
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
        assertTrue(message.getText().contains(
                "Your FitSphere password reset OTP is:"
        ));
    }

    @Test
    void verifyPasswordResetOtp_success() {

        when(valueOperations.get(
                "password-reset:john@gmail.com"
        )).thenReturn("123456");

        emailService.verifyPasswordResetOtp(
                "john@gmail.com",
                "123456"
        );

        verify(redisTemplate)
                .delete("password-reset:john@gmail.com");
    }

    @Test
    void verifyPasswordResetOtp_throwsWhenOtpNotFound() {

        when(valueOperations.get(
                "password-reset:john@gmail.com"
        )).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailService.verifyPasswordResetOtp(
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
    void verifyPasswordResetOtp_throwsWhenOtpInvalid() {

        when(valueOperations.get(
                "password-reset:john@gmail.com"
        )).thenReturn("999999");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailService.verifyPasswordResetOtp(
                        "john@gmail.com",
                        "123456"
                )
        );

        assertEquals(
                "Invalid OTP",
                exception.getMessage()
        );

        verify(redisTemplate, never()).delete(anyString());
    }

    // =========================
    // SEND EMAIL
    // =========================

    @Test
    void sendEmail_success() {

        emailService.sendEmail(
                "john@gmail.com",
                "Test Subject",
                "Test message"
        );

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals("john@gmail.com", message.getTo()[0]);
        assertEquals("Test Subject", message.getSubject());
        assertEquals("Test message", message.getText());
    }
}