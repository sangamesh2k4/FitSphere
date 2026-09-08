package com.sangamesh.Fitsphere.repository;

import com.sangamesh.Fitsphere.entity.BodyMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface BodyMeasurementRepository extends JpaRepository <BodyMeasurement,Long > {

    List<BodyMeasurement> findByUserIdOrderByRecordedAtDescIdDesc(Long userId);
    Page<BodyMeasurement> findByUserIdOrderByRecordedAtDescIdDesc(Long userId, Pageable pageable);
    List<BodyMeasurement> findByUserIdOrderByRecordedAtAscIdAsc(Long userId);

    Optional<BodyMeasurement>  findFirstByUserIdOrderByRecordedAtDescIdDesc(Long userId);

    Optional<BodyMeasurement> findFirstByUserIdOrderByRecordedAtDesc(Long userId);

    Optional<BodyMeasurement> findByIdAndUserId(Long id, Long userId);

    List<BodyMeasurement> findByUserIdOrderByRecordedAtAsc(Long userId);
}

