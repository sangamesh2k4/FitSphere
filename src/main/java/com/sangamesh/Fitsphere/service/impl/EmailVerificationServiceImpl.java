package com.sangamesh.Fitsphere.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.auth.PendingRegistrationDto;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Role;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.repository.UserRepository;
import com.sangamesh.Fitsphere.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    private static final Duration VERIFICATION_EXPIRY =
            Duration.ofMinutes(10);

    private final SecureRandom secureRandom =
            new SecureRandom();

    @Override
    public void startVerification(PendingRegistrationDto pending) {
        if (userRepository.existsByEmail(pending.getEmail())) {
            throw new BadRequestException(
                    "Email is already registered");
        }
        if (userRepository.existsByUsername(pending.getUsername())) {
            throw new BadRequestException(
                    "Username is already taken");
        }

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        String otpKey = "email-verification:" + pending.getEmail();
        String pendingKey = "pending-registration:" + pending.getEmail();

        try {
            String pendingJson = objectMapper.writeValueAsString(pending);

            redisTemplate.opsForValue().set(pendingKey, pendingJson, VERIFICATION_EXPIRY);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Failed to store pending registration");
        }

        redisTemplate.opsForValue().set(otpKey, otp, VERIFICATION_EXPIRY);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(pending.getEmail());
        message.setSubject("FitSphere Email Verification");

        message.setText("Your FitSphere verification OTP is: " + otp + "\n\nThis OTP expires in 10 minutes.");
        mailSender.send(message);
    }

    @Override
    public User verifyRegistration(String email, String otp) {
        String otpKey = "email-verification:" + email;
        String pendingKey = "pending-registration:" + email;
        String storedOtp = redisTemplate.opsForValue().get(otpKey);

        if (storedOtp == null) {
            throw new BadRequestException(
                    "OTP expired or not found");
        }

        if (!storedOtp.equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        String pendingJson = redisTemplate.opsForValue().get(pendingKey);
        if (pendingJson == null) {
            throw new BadRequestException(
                    "Registration session expired");
        }

        try {
            PendingRegistrationDto pending =
                    objectMapper.readValue(pendingJson,
                            PendingRegistrationDto.class);

            User user = new User();
            user.setUsername(pending.getUsername());
            user.setEmail(pending.getEmail());
            user.setPassword(pending.getEncodedPassword());
            user.setRole(Role.ROLE_USER);
            user.setEmailVerified(true);

            User savedUser = userRepository.save(user);
            redisTemplate.delete(otpKey);
            redisTemplate.delete(pendingKey);
            return savedUser;
        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to read pending registration");
        }
    }


    @Override
    public void resendVerification(String email) {

        String pendingKey = "pending-registration:" + email;

        if (!Boolean.TRUE.equals(redisTemplate.hasKey(pendingKey))) {
            throw new BadRequestException(
                    "No pending registration found or it has expired.");
        }

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        String otpKey = "email-verification:" + email;

        redisTemplate.opsForValue().set(otpKey, otp, VERIFICATION_EXPIRY);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("FitSphere Email Verification");
        message.setText(
                "Your FitSphere verification OTP is: " + otp +
                        "\n\nThis OTP expires in 10 minutes."
        );

        mailSender.send(message);
    }
}

