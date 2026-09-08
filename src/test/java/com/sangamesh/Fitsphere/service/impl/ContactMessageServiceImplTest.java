package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.dto.contact.ContactMessageRequestDto;
import com.sangamesh.Fitsphere.entity.ContactMessage;
import com.sangamesh.Fitsphere.enums.ContactReason;
import com.sangamesh.Fitsphere.repository.ContactMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactMessageServiceImplTest {

    @Mock
    private ContactMessageRepository contactMessageRepository;

    private ContactMessageServiceImpl contactMessageService;

    @BeforeEach
    void setUp() {
        contactMessageService = new ContactMessageServiceImpl(contactMessageRepository);
    }

    @Test
    void submit_shouldSaveContactMessage() {

        ContactMessageRequestDto request = new ContactMessageRequestDto();
        request.setEmail("user@example.com");
        request.setReason(ContactReason.BUG_REPORT);
        request.setMessage("There is a bug in the application.");

        contactMessageService.submit(request);

        ArgumentCaptor<ContactMessage> captor =
                ArgumentCaptor.forClass(ContactMessage.class);

        verify(contactMessageRepository).save(captor.capture());

        ContactMessage saved = captor.getValue();

        assertEquals("user@example.com", saved.getEmail());
        assertEquals(ContactReason.BUG_REPORT, saved.getReason());
        assertEquals("There is a bug in the application.", saved.getMessage());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void submit_shouldSaveOtherReasonWithMessage() {

        ContactMessageRequestDto request = new ContactMessageRequestDto();
        request.setEmail("user@example.com");
        request.setReason(ContactReason.OTHER);
        request.setMessage("My issue is not listed.");

        contactMessageService.submit(request);

        verify(contactMessageRepository).save(any(ContactMessage.class));
    }

    @Test
    void submit_whenOtherReasonAndMessageIsNull_shouldThrowException() {

        ContactMessageRequestDto request = new ContactMessageRequestDto();
        request.setEmail("user@example.com");
        request.setReason(ContactReason.OTHER);
        request.setMessage(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contactMessageService.submit(request)
        );

        assertEquals("Please describe your issue", exception.getMessage());

        verify(contactMessageRepository, never()).save(any(ContactMessage.class));
    }

    @Test
    void submit_whenOtherReasonAndMessageIsBlank_shouldThrowException() {

        ContactMessageRequestDto request = new ContactMessageRequestDto();
        request.setEmail("user@example.com");
        request.setReason(ContactReason.OTHER);
        request.setMessage("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contactMessageService.submit(request)
        );

        assertEquals("Please describe your issue", exception.getMessage());

        verify(contactMessageRepository, never()).save(any(ContactMessage.class));
    }
}