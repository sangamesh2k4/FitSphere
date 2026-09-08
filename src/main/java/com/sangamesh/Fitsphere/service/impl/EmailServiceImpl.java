package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.repository.UserRepository;
import com.sangamesh.Fitsphere.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private static final Duration OTP_EXPIRY = Duration.ofMinutes(10);
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void sendVerificationOtp(String email) {
        sendOtp(
                email,
                "email-verification:",
                "FitSphere Email Verification",
                "Your FitSphere verification OTP is: "
        );
    }

    @Override
    public void verifyEmailOtp(String email, String otp) {

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException(
                    "Email is already registered");
        }

        verifyOtp(email, otp, "email-verification:");
    }

    @Override
    public void sendPasswordResetOtp(String email) {
        sendOtp(email, "password-reset:", "FitSphere Password Reset", "Your FitSphere password reset OTP is: ");
    }

    @Override
    public void verifyPasswordResetOtp(String email, String otp) {
        verifyOtp(email, otp, "password-reset:");
    }

    private void sendOtp(String email, String keyPrefix, String subject, String text) {
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        String key = keyPrefix + email;

        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRY);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);

        message.setText(text + otp + "\n\nThis OTP expires in 10 minutes."
        );

        mailSender.send(message);
    }

    private void verifyOtp(String email, String otp, String keyPrefix) {
        String key = keyPrefix + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {throw new BadRequestException("OTP expired or not found");
        }
        if (!storedOtp.equals(otp)) {throw new BadRequestException("Invalid OTP");
        }

        redisTemplate.delete(key);
    }


    @Override
    public void sendEmail(String to, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message);
        mailSender.send(mail);
    }
}