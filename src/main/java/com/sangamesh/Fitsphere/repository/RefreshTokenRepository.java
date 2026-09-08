package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);


    @Transactional
    @Modifying
    void deleteByToken(String token);

    @Transactional
    @Modifying
    void deleteByUser_Id(Long userId);

}
