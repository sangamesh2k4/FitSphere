package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.account.*;
import com.sangamesh.Fitsphere.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PatchMapping("/username")
    public String changeUsername(@RequestBody ChangeUsernameRequestDto request) {
        accountService.changeUsername(request.getNewUsername());
        return "Username changed successfully";
    }

    @GetMapping("/username/check")
    public ResponseEntity<ChangeEmailRequestDto.UsernameAvailabilityResponseDto> checkUsername(
            @RequestParam String username) {

        return ResponseEntity.ok(
                accountService.checkUsernameAvailability(username)
        );
    }
    @PatchMapping("/password")
    public String changePassword(@RequestBody ChangePasswordRequestDto request) {
        accountService.changePassword(
                request.getCurrentPassword(),
                request.getNewPassword());

        return "Password changed successfully";
    }

    @PostMapping("/email/otp")
    public String sendEmailChangeOtp(@RequestBody ChangeEmailRequestDto request) {
        accountService.sendEmailChangeOtp(request.getNewEmail());
        return "OTP sent to new email";
    }

    @PatchMapping("/email")
    public String changeEmail(@RequestBody VerifyEmailChangeRequestDto request) {
        accountService.verifyAndChangeEmail(
                request.getNewEmail(),
                request.getOtp());
        return "Email changed successfully";
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponseDto> getCurrentUser() {
        return ResponseEntity.ok(accountService.getCurrentUser()
        );
    }
}
