package com.sangamesh.Fitsphere.entity;

import com.sangamesh.Fitsphere.enums.ContactMessageStatus;
import com.sangamesh.Fitsphere.enums.ContactReason;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ContactMessageTest {

    @Test
    void defaultConstructor_shouldSetStatusToOpen() {

        ContactMessage message = new ContactMessage();

        assertEquals(ContactMessageStatus.OPEN, message.getStatus());
    }

    @Test
    void settersAndGetters_shouldWork() {

        ContactMessage message = new ContactMessage();

        LocalDateTime createdAt = LocalDateTime.now();

        message.setId(1L);
        message.setEmail("user@example.com");
        message.setReason(ContactReason.BUG_REPORT);
        message.setMessage("There is a bug.");
        message.setCreatedAt(createdAt);
        message.setStatus(ContactMessageStatus.IN_PROGRESS);

        assertEquals(1L, message.getId());
        assertEquals("user@example.com", message.getEmail());
        assertEquals(ContactReason.BUG_REPORT, message.getReason());
        assertEquals("There is a bug.", message.getMessage());
        assertEquals(createdAt, message.getCreatedAt());
        assertEquals(ContactMessageStatus.IN_PROGRESS, message.getStatus());
    }

    @Test
    void status_shouldBeChangeable() {

        ContactMessage message = new ContactMessage();

        assertEquals(ContactMessageStatus.OPEN, message.getStatus());

        message.setStatus(ContactMessageStatus.RESOLVED);

        assertEquals(ContactMessageStatus.RESOLVED, message.getStatus());
    }
}
