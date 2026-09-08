package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.FoodLog;
import com.sangamesh.Fitsphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FoodLogRepository
        extends JpaRepository<FoodLog, Long> {

    List<FoodLog> findByUserAndLogDate(User user, LocalDate logDate);

    Optional<FoodLog> findByIdAndUser(Long id, User user);

    List<FoodLog> findByUserAndLogDateBetweenOrderByLogDateDesc(User user, LocalDate startDate, LocalDate endDate );

    long countByLogDate(LocalDate logDate);

}