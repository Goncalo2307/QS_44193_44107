package com.eduscrum.qs.backend.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "achievements",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_achievements_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String icon;

    @Column(nullable = false)
    private Integer points;
}
