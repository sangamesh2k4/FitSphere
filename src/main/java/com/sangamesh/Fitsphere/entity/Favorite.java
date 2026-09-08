package com.sangamesh.Fitsphere.entity;

import com.sangamesh.Fitsphere.enums.FavoriteType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(
        name = "favorites",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "exercise_id"}),
                @UniqueConstraint(columnNames = {"user_id", "fdc_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FavoriteType type;

    // Used only for exercise favorites
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    // Used only for food favorites
    @Column(name = "fdc_id")
    private Long fdcId;


    private LocalDateTime createdAt;
}