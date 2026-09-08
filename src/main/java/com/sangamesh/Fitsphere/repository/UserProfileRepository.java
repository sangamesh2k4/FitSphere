package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository
        extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser(User user);
}