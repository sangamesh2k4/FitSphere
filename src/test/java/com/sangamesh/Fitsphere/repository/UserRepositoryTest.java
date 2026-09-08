package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setUsername("john");
        user.setEmail("john@gmail.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setEmailVerified(true);

        user = userRepository.save(user);
    }

    @Test
    void findByUsername_returnsUser() {

        Optional<User> result =
                userRepository.findByUsername("john");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    @Test
    void findByUsername_returnsEmptyWhenNotFound() {

        Optional<User> result =
                userRepository.findByUsername("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByEmail_returnsUser() {

        Optional<User> result =
                userRepository.findByEmail("john@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("john@gmail.com", result.get().getEmail());
    }

    @Test
    void findByEmail_returnsEmptyWhenNotFound() {

        Optional<User> result =
                userRepository.findByEmail("unknown@gmail.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByUsername_returnsTrueWhenExists() {

        assertTrue(userRepository.existsByUsername("john"));
    }

    @Test
    void existsByUsername_returnsFalseWhenNotExists() {

        assertFalse(userRepository.existsByUsername("unknown"));
    }

    @Test
    void existsByEmail_returnsTrueWhenExists() {

        assertTrue(userRepository.existsByEmail("john@gmail.com"));
    }

    @Test
    void existsByEmail_returnsFalseWhenNotExists() {

        assertFalse(userRepository.existsByEmail("unknown@gmail.com"));
    }

    @Test
    void findByUsernameOrEmail_findsByUsername() {

        Optional<User> result =
                userRepository.findByUsernameOrEmail(
                        "john",
                        "john"
                );

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void findByUsernameOrEmail_findsByEmail() {

        Optional<User> result =
                userRepository.findByUsernameOrEmail(
                        "john@gmail.com",
                        "john@gmail.com"
                );

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void findByUsernameOrEmail_returnsEmptyWhenNotFound() {

        Optional<User> result =
                userRepository.findByUsernameOrEmail(
                        "unknown",
                        "unknown@gmail.com"
                );

        assertTrue(result.isEmpty());
    }
}