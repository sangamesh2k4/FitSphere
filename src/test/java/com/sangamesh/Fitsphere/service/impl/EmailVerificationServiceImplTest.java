package com.sangamesh.Fitsphere.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.auth.PendingRegistrationDto;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Role;
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

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    private final PendingRegistrationDto pending = createPendingDto();

    private static PendingRegistrationDto createPendingDto() {
        PendingRegistrationDto dto = new PendingRegistrationDto();
        dto.setUsername("john");
        dto.setEmail("john@gmail.com");
        dto.setEncodedPassword("encodedPassword");
        return dto;
    }

    // =========================
    // START VERIFICATION
    // =========================

    @Test
    void startVerification_success() throws Exception {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("john"))
                .thenReturn(false);

        when(objectMapper.writeValueAsString(pending))
                .thenReturn("pending-json");

        emailVerificationService.startVerification(pending);

        verify(valueOperations).set(
                eq("pending-registration:john@gmail.com"),
                eq("pending-json"),
                eq(Duration.ofMinutes(10))
        );

        verify(valueOperations).set(
                eq("email-verification:john@gmail.com"),
                anyString(),
                eq(Duration.ofMinutes(10))
        );

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void startVerification_throwsWhenEmailAlreadyExists() throws Exception {

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailVerificationService.startVerification(pending)
        );

        assertEquals("Email is already registered",
                exception.getMessage());

        verify(userRepository, never()).existsByUsername(anyString());
        verify(objectMapper, never()).writeValueAsString(any());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void startVerification_throwsWhenUsernameAlreadyExists() throws Exception {

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("john"))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailVerificationService.startVerification(pending)
        );

        assertEquals("Username is already taken",
                exception.getMessage());

        verify(objectMapper, never()).writeValueAsString(any());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void startVerification_throwsWhenPendingRegistrationCannotBeSerialized()
            throws Exception {

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("john"))
                .thenReturn(false);

        when(objectMapper.writeValueAsString(pending))
                .thenThrow(new JsonProcessingException("serialization failed") {});

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailVerificationService.startVerification(pending)
        );

        assertEquals(
                "Failed to store pending registration",
                exception.getMessage()
        );

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    // =========================
    // VERIFY REGISTRATION
    // =========================

    @Test
    void verifyRegistration_success() throws Exception {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("email-verification:john@gmail.com"))
                .thenReturn("123456");

        when(valueOperations.get("pending-registration:john@gmail.com"))
                .thenReturn("pending-json");

        when(objectMapper.readValue(
                "pending-json",
                PendingRegistrationDto.class
        )).thenReturn(pending);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("john");
        savedUser.setEmail("john@gmail.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.ROLE_USER);
        savedUser.setEmailVerified(true);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = emailVerificationService.verifyRegistration(
                "john@gmail.com",
                "123456"
        );

        assertSame(savedUser, result);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User createdUser = captor.getValue();

        assertEquals("john", createdUser.getUsername());
        assertEquals("john@gmail.com", createdUser.getEmail());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals(Role.ROLE_USER, createdUser.getRole());
        assertTrue(createdUser.getEmailVerified());

        verify(redisTemplate)
                .delete("email-verification:john@gmail.com");

        verify(redisTemplate)
                .delete("pending-registration:john@gmail.com");
    }

    @Test
    void verifyRegistration_throwsWhenOtpNotFound() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("email-verification:john@gmail.com"))
                .thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailVerificationService.verifyRegistration(
                        "john@gmail.com",
                        "123456"
                )
        );

        assertEquals(
                "OTP expired or not found",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void verifyRegistration_throwsWhenOtpIsInvalid() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("email-verification:john@gmail.com"))
                .thenReturn("999999");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailVerificationService.verifyRegistration(
                        "john@gmail.com",
                        "123456"
                )
        );

        assertEquals("Invalid OTP", exception.getMessage());

        verify(valueOperations, never())
                .get("pending-registration:john@gmail.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    void verifyRegistration_throwsWhenPendingRegistrationNotFound() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("email-verification:john@gmail.com"))
                .thenReturn("123456");

        when(valueOperations.get("pending-registration:john@gmail.com"))
                .thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailVerificationService.verifyRegistration(
                        "john@gmail.com",
                        "123456"
                )
        );

        assertEquals(
                "Registration session expired",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void verifyRegistration_throwsWhenPendingRegistrationCannotBeDeserialized()
            throws Exception {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("email-verification:john@gmail.com"))
                .thenReturn("123456");

        when(valueOperations.get("pending-registration:john@gmail.com"))
                .thenReturn("invalid-json");

        when(objectMapper.readValue(
                "invalid-json",
                PendingRegistrationDto.class
        )).thenThrow(new JsonProcessingException("invalid json") {});

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailVerificationService.verifyRegistration(
                        "john@gmail.com",
                        "123456"
                )
        );

        assertEquals(
                "Failed to read pending registration",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any());
    }

    // =========================
    // RESEND VERIFICATION
    // =========================

    @Test
    void resendVerification_success() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(redisTemplate.hasKey(
                "pending-registration:john@gmail.com"
        )).thenReturn(true);

        emailVerificationService.resendVerification("john@gmail.com");

        verify(valueOperations).set(
                eq("email-verification:john@gmail.com"),
                anyString(),
                eq(Duration.ofMinutes(10))
        );

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void resendVerification_throwsWhenPendingRegistrationNotFound() {

        when(redisTemplate.hasKey(
                "pending-registration:john@gmail.com"
        )).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> emailVerificationService.resendVerification(
                        "john@gmail.com"
                )
        );

        assertEquals(
                "No pending registration found or it has expired.",
                exception.getMessage()
        );

        verify(mailSender, never()).send(any(SimpleMailMessage.class));

        verify(valueOperations, never()).set(
                anyString(),
                anyString(),
                any(Duration.class)
        );
    }
}