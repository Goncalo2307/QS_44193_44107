package com.eduscrum.qs.backend.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "account_achievements",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_achievements_pair",
                        columnNames = {"account_id", "achievement_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_achievements_account"))
    private Account account;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_achievements_achievement"))
    private Achievement achievement;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;
}
