package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.Exercise;
import com.sangamesh.Fitsphere.entity.Favorite;
import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.enums.FavoriteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndExercise(User user, Exercise exercise);

    Optional<Favorite> findByUserAndExercise(User user, Exercise exercise);

    //foods
    boolean existsByUserAndFdcId(User user, Long fdcId);

    Optional<Favorite> findByUserAndFdcId(User user, Long fdcId);

    List<Favorite> findByUserAndFdcIdIsNotNull(User user);


    List<Favorite> findByUserAndType(User user, FavoriteType type);
}