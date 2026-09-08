package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.auth.*;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.AuthService;
import com.sangamesh.Fitsphere.service.EmailVerificationService;
import com.sangamesh.Fitsphere.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and JWT management")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;


    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @Operation(summary = "Login and receive JWT tokens")
    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequest request){
        return authService.login(request);
    }



    @Hidden
    @GetMapping("/test")
    public String testToken(@RequestParam String token){

        return jwtService.extractTokenType(token);
    }

    @Hidden
    @GetMapping("/test/access")
    public String testAccess(@RequestParam String token) {
        return jwtService.extractTokenType(token);
    }

    @Hidden
    @GetMapping("/test/subject")
    public String testSubject(@RequestParam String token) {
        return jwtService.extractSubject(token);
    }

    @Hidden
    @GetMapping("/test/expiry")
    public Date testExpiry(@RequestParam String token) {
        return jwtService.extractExpiration(token);
    }


    @Operation(summary = "Generate a new access token using refresh token")
    @PostMapping("/refresh")
    public AccessTokenResponseDto refresh(@RequestBody RefreshTokenRequestDto request){

        return authService.refreshAccessToken(request);
    }

    @Operation(summary = "Logout user and revoke refresh token")
    @PostMapping("/logout")
    public String logout(@RequestBody LogOutRequestDto request){
        authService.Logout(request);
        return "Logged out successfully";
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestBody VerifyEmailRequestDto request) {
        emailVerificationService.verifyRegistration(
                request.getEmail(),
                request.getOtp());
        return "Email verified and account created successfully";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequestDto request) {
        passwordResetService.sendResetOtp(request.getEmail());
        return "Password reset OTP sent successfully";
    }

    @PostMapping("/verify-reset-otp")
    public String verifyResetOtp(@RequestBody VerifyResetOtpRequestDto request) {
        return passwordResetService.verifyResetOtp(request.getEmail(), request.getOtp());
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequestDto request) {
        passwordResetService.resetPassword(request.getResetToken(), request.getNewPassword());
        return "Password reset successfully";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestBody EmailRequestDto request) {
        emailVerificationService.resendVerification(request.getEmail());
        return "Verification OTP sent successfully";
    }
}
