package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.auth.PendingRegistrationDto;
import com.sangamesh.Fitsphere.entity.User;

public interface EmailVerificationService {

    void startVerification(PendingRegistrationDto pendingRegistration);

    User verifyRegistration(String email, String otp);

    void resendVerification(String email);
}