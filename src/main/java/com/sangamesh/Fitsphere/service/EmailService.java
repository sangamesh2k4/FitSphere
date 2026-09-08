package com.sangamesh.Fitsphere.service;

public interface EmailService {

    void sendVerificationOtp(String email);

    void verifyEmailOtp(String email, String otp);

    void sendPasswordResetOtp(String email);

    void verifyPasswordResetOtp(String email, String otp);

    void sendEmail(String to, String subject, String message);



}
