package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.RefreshToken;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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
    void findByToken_returnsToken() {

        RefreshToken token = new RefreshToken();
        token.setToken("refresh-token");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(token);

        Optional<RefreshToken> result =
                refreshTokenRepository.findByToken("refresh-token");

        assertTrue(result.isPresent());
        assertEquals("refresh-token", result.get().getToken());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    void findByToken_returnsEmptyWhenTokenDoesNotExist() {

        Optional<RefreshToken> result =
                refreshTokenRepository.findByToken("invalid-token");

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteByToken_deletesMatchingToken() {

        RefreshToken token = new RefreshToken();
        token.setToken("refresh-token");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(token);

        refreshTokenRepository.deleteByToken("refresh-token");

        assertTrue(
                refreshTokenRepository
                        .findByToken("refresh-token")
                        .isEmpty()
        );
    }

    @Test
    void deleteByUserId_deletesAllTokensOfUser() {

        RefreshToken token1 = new RefreshToken();
        token1.setToken("refresh-token-1");
        token1.setUser(user);
        token1.setExpiryDate(LocalDateTime.now().plusDays(7));

        RefreshToken token2 = new RefreshToken();
        token2.setToken("refresh-token-2");
        token2.setUser(user);
        token2.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(token1);
        refreshTokenRepository.save(token2);

        refreshTokenRepository.deleteByUser_Id(user.getId());

        assertTrue(
                refreshTokenRepository
                        .findByToken("refresh-token-1")
                        .isEmpty()
        );

        assertTrue(
                refreshTokenRepository
                        .findByToken("refresh-token-2")
                        .isEmpty()
        );
    }
}
