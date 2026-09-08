package com.sangamesh.Fitsphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    private Boolean completed = false;

    private Double totalVolume;

    @OrderBy("exerciseOrder ASC")
    @OneToMany(
            mappedBy = "workoutSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WorkoutExercise> workoutExercises =
            new ArrayList<>();

    @PrePersist
    public void prePersist() {

        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }

        if (completed == null) {
            completed = false;
        }

        if (totalVolume == null) {
            totalVolume = 0.0;
        }
    }
}