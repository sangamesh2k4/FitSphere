package com.sangamesh.Fitsphere.service;


import com.sangamesh.Fitsphere.dto.auth.*;
import com.sangamesh.Fitsphere.entity.RefreshToken;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Role;
import com.sangamesh.Fitsphere.exception.BadRequestException;
import com.sangamesh.Fitsphere.exception.DuplicateResourceException;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.security.TokenType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sangamesh.Fitsphere.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;

    //register
    public String register(@RequestBody RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new DuplicateResourceException("username already exists");
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email already exists");
        }
        PendingRegistrationDto pending = new PendingRegistrationDto();
        pending.setUsername(request.getUsername());
        pending.setEmail(request.getEmail());
        pending.setEncodedPassword(passwordEncoder.encode(request.getPassword()));
        emailVerificationService.startVerification(pending);
        return "Verification OTP sent to email";
    }

    public AuthResponseDto login(LoginRequest request){

        User user=userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(()->new RuntimeException("Invalid username or Email"));
        if(!user.getEnabled()){
            throw new RuntimeException("your account is suspended");
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid password");
        }
        String accessToken=jwtService.generateAccessToken(user);
        String refreshToken=jwtService.generateRefreshToken(user);
        refreshTokenService.saveRefreshToken(refreshToken,user,jwtService.getRefreshTokenExpiry());
        return new AuthResponseDto(accessToken,refreshToken,user.getRole());
    }


    //delete account
    public void deleteAccount(Long userId){
        if(!userRepository.existsById(userId)){
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    //refresh access token
    public AccessTokenResponseDto refreshAccessToken(RefreshTokenRequestDto request){
        RefreshToken refreshToken=refreshTokenService.validateRefreshToken(request.getRefreshToken());
        String token=refreshToken.getToken();
        if(!jwtService.hasTokenType(token, TokenType.REFRESH)){
            throw new RuntimeException("Invalid Refresh token");
        }
        Long userId=Long.parseLong(jwtService.extractSubject(token));
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("user not found"));
        String accessToken=jwtService.generateAccessToken(user);
        return new AccessTokenResponseDto(accessToken);
    }

    @Transactional
    public void Logout(LogOutRequestDto request){
        refreshTokenService.deleteRefreshToken(request.getRefreshToken());
    }




}

