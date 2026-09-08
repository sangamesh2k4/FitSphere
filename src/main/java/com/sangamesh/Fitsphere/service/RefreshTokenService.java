package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.entity.RefreshToken;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.exception.ResourceNotFoundException;
import com.sangamesh.Fitsphere.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveRefreshToken(String token, User user, LocalDateTime expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiryDate);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token){
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
    }

    public RefreshToken validateRefreshToken(String token){
        RefreshToken refreshToken = findByToken(token);
        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new ResourceNotFoundException("Refresh token expired");
        }
        return refreshToken;
    }
    public void deleteRefreshToken(String token){
        refreshTokenRepository.deleteByToken(token);
    }


    public void deleteAllUserTokens(Long userId) {
        refreshTokenRepository.deleteByUser_Id(userId);
    }
}
