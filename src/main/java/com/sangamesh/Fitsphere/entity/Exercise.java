package com.sangamesh.Fitsphere.entity;

import com.sangamesh.Fitsphere.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exercise_library")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Muscle primaryMuscle;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "exercise_secondary_muscles",
            joinColumns = @JoinColumn(name = "exercise_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "muscle")
    private List<Muscle> secondaryMuscles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementPattern movementPattern;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Equipment equipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseType exerciseType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(
            mappedBy = "exercise",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExerciseInstruction> instructions;

    @OneToMany(
            mappedBy = "exercise",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExerciseTip> tips;

    @OneToMany(
            mappedBy = "exercise",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExerciseCommonMistake> commonMistakes;

    //@NotBlank
    private String imageUrl;

    @Column(nullable = false)
    private Boolean active = true;
}