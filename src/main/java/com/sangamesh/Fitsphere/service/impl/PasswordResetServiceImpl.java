package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.repository.UserRepository;
import com.sangamesh.Fitsphere.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private static final Duration OTP_EXPIRY = Duration.ofMinutes(10);

    private static final Duration RESET_TOKEN_EXPIRY = Duration.ofMinutes(10);

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void sendResetOtp(String email) {

        if (!userRepository.existsByEmail(email)) {
            throw new BadRequestException(
                    "User not found with this email");
        }
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        String key = "password-reset-otp:" + email;

        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRY);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("FitSphere Password Reset");

        message.setText("Your FitSphere password reset OTP is: " + otp + "\n\nThis OTP expires in 10 minutes."
        );
        mailSender.send(message);
    }

    @Override
    public String verifyResetOtp(String email, String otp) {
        String otpKey = "password-reset-otp:" + email;
        String storedOtp = redisTemplate.opsForValue().get(otpKey);

        if (storedOtp == null) {
            throw new BadRequestException(
                    "OTP expired or not found");
        }

        if (!storedOtp.equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        String resetToken = UUID.randomUUID().toString();
        String resetTokenKey = "password-reset-token:" + resetToken;

        redisTemplate.opsForValue().set(resetTokenKey, email, RESET_TOKEN_EXPIRY);
        redisTemplate.delete(otpKey);
        return resetToken;
    }

    @Override
    public void resetPassword(String resetToken, String newPassword) {
        String tokenKey = "password-reset-token:" + resetToken;
        String email = redisTemplate.opsForValue().get(tokenKey);
        if (email == null) {throw new BadRequestException("Reset token expired or invalid");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new BadRequestException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redisTemplate.delete(tokenKey);
    }

}