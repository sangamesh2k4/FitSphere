package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.contact.ContactMessageRequestDto;
import com.sangamesh.Fitsphere.entity.ContactMessage;
import com.sangamesh.Fitsphere.enums.ContactReason;
import com.sangamesh.Fitsphere.repository.ContactMessageRepository;
import com.sangamesh.Fitsphere.service.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    @Override
    public void submit(ContactMessageRequestDto request) {

        if (request.getReason() == ContactReason.OTHER &&
                (request.getMessage() == null || request.getMessage().isBlank())) {
            throw new IllegalArgumentException("Please describe your issue");
        }

        ContactMessage contactMessage = new ContactMessage();

        contactMessage.setEmail(request.getEmail());
        contactMessage.setReason(request.getReason());
        contactMessage.setMessage(request.getMessage());
        contactMessage.setCreatedAt(LocalDateTime.now());

        contactMessageRepository.save(contactMessage);
    }
}