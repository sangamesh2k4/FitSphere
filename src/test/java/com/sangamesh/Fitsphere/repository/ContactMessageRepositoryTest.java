package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.ContactMessage;
import com.sangamesh.Fitsphere.enums.ContactReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never"
})
class ContactMessageRepositoryTest {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @BeforeEach
    void setUp() {
        contactMessageRepository.deleteAll();

        ContactMessage message1 = new ContactMessage();
        message1.setEmail("user1@example.com");
        message1.setReason(ContactReason.BUG_REPORT);
        message1.setMessage("There is a bug.");
        message1.setCreatedAt(LocalDateTime.now());

        ContactMessage message2 = new ContactMessage();
        message2.setEmail("user2@example.com");
        message2.setReason(ContactReason.ACCOUNT_ISSUE);
        message2.setMessage("I have an account issue.");
        message2.setCreatedAt(LocalDateTime.now());

        contactMessageRepository.saveAll(List.of(message1, message2));
    }

    @Test
    void save_shouldPersistContactMessage() {

        ContactMessage message = new ContactMessage();
        message.setEmail("new@example.com");
        message.setReason(ContactReason.OTHER);
        message.setMessage("Something else.");
        message.setCreatedAt(LocalDateTime.now());

        ContactMessage saved = contactMessageRepository.save(message);

        assertNotNull(saved.getId());
        assertEquals(
                com.sangamesh.Fitsphere.enums.ContactMessageStatus.OPEN,
                saved.getStatus()
        );
    }

    @Test
    void findById_shouldReturnMessage() {

        ContactMessage message =
                contactMessageRepository.findAll().get(0);

        Optional<ContactMessage> result =
                contactMessageRepository.findById(message.getId());

        assertTrue(result.isPresent());
        assertEquals(message.getEmail(), result.get().getEmail());
    }

    @Test
    void findAll_shouldReturnAllMessages() {

        List<ContactMessage> messages =
                contactMessageRepository.findAll();

        assertEquals(2, messages.size());
    }

    @Test
    void delete_shouldRemoveMessage() {

        ContactMessage message =
                contactMessageRepository.findAll().get(0);

        contactMessageRepository.delete(message);

        assertFalse(
                contactMessageRepository.findById(message.getId()).isPresent()
        );
    }
}