package com.sangamesh.Fitsphere.controller;


import com.sangamesh.Fitsphere.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/otp")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public String sendOtp(@RequestParam String email) {
        emailService.sendVerificationOtp(email);
        return "OTP sent successfully";
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp) {
        emailService.verifyEmailOtp(email, otp);
        return "OTP verified successfully";
    }
}
