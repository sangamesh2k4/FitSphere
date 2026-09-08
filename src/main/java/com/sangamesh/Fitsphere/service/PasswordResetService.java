package com.sangamesh.Fitsphere.service;

public interface PasswordResetService {

    void sendResetOtp(String email);

    String verifyResetOtp(String email, String otp);

    void resetPassword(String resetToken, String newPassword);
}