package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.account.ChangeEmailRequestDto;
import com.sangamesh.Fitsphere.dto.account.CurrentUserResponseDto;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.exception.*;
import com.sangamesh.Fitsphere.repository.UserRepository;
import com.sangamesh.Fitsphere.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final RefreshTokenService refreshTokenService;

    private static final Duration OTP_EXPIRY = Duration.ofMinutes(10);

    private final SecureRandom secureRandom =
            new SecureRandom();

    @Override
    public void changeUsername(String newUsername) {
        User user = userService.getCurrentUser();
        if (userRepository.existsByUsername(newUsername)) {
            throw new DuplicateResourceException(
                    "Username already exists");
        }
        user.setUsername(newUsername);
        userRepository.save(user);
    }
    @Override
    public ChangeEmailRequestDto.UsernameAvailabilityResponseDto checkUsernameAvailability(String username) {

        boolean available = !userRepository.existsByUsername(username);

        return new ChangeEmailRequestDto.UsernameAvailabilityResponseDto(available);
    }

    @Override
    public void changePassword(String currentPassword, String newPassword) {
        User user = userService.getCurrentUser();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {

            throw new BadRequestException(
                    "Current password is incorrect");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException(
                    "New password cannot be same as current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        refreshTokenService.deleteAllUserTokens(user.getId());
    }

    @Override
    public void sendEmailChangeOtp(String newEmail) {
        User user = userService.getCurrentUser();
        if (userRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email already exists");
        }
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        String key = "email-change:" + user.getId() + ":" + newEmail;
        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRY);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(newEmail);
        message.setSubject("FitSphere Email Change Verification");
        message.setText("Your FitSphere email change OTP is: " + otp + "\n\nThis OTP expires in 10 minutes.");
        mailSender.send(message);
    }

    @Override
    public void verifyAndChangeEmail(String newEmail, String otp) {
        User user = userService.getCurrentUser();
        String key = "email-change:" + user.getId() + ":" + newEmail;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            throw new BadRequestException("OTP expired or not found");
        }
        if (!storedOtp.equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }
        if (userRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException(
                    "Email already exists");
        }
        user.setEmail(newEmail);
        userRepository.save(user);
        refreshTokenService.deleteAllUserTokens(user.getId());
        redisTemplate.delete(key);
    }

    @Override
    public CurrentUserResponseDto getCurrentUser() {
        User user = userService.getCurrentUser();
        return new CurrentUserResponseDto(
                user.getUsername(),
                user.getEmail()
        );
    }
}