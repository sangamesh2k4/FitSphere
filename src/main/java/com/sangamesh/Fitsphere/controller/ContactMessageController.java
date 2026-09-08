package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.contact.ContactMessageRequestDto;
import com.sangamesh.Fitsphere.service.ContactMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    @PostMapping
    public ResponseEntity<String> submitContactMessage(
            @Valid @RequestBody ContactMessageRequestDto request) {

        contactMessageService.submit(request);

        return ResponseEntity.ok("Message sent successfully");
    }
}