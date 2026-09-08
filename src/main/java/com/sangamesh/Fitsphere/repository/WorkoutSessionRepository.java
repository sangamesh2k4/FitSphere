package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    Optional<WorkoutSession> findByIdAndUser(Long id, User user);

    List<WorkoutSession> findByUserOrderByStartedAtDesc(User user);

    Optional<WorkoutSession> findFirstByUserAndCompletedFalseOrderByStartedAtDesc(User user);

    List<WorkoutSession> findByUserAndCompletedTrueOrderByStartedAtDesc(User user);

    List<WorkoutSession>findByUserAndCompletedTrueAndStartedAtBetweenOrderByStartedAtAsc(User user, LocalDateTime start, LocalDateTime end);
}