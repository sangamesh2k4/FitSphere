package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.dto.account.ChangeEmailRequestDto;
import com.sangamesh.Fitsphere.dto.account.CurrentUserResponseDto;

public interface AccountService {

    void changeUsername(String newUsername);
    ChangeEmailRequestDto.UsernameAvailabilityResponseDto checkUsernameAvailability(String username);

    void changePassword(String currentPassword, String newPassword);

    void sendEmailChangeOtp(String newEmail);

    void verifyAndChangeEmail(String newEmail, String otp);

    CurrentUserResponseDto getCurrentUser();
}
