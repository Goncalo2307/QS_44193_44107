package com.eduscrum.qs.backend.domain.model;

import com.eduscrum.qs.backend.domain.enums.ScrumRoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "team_assignments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_team_assignments_pair", columnNames = {"team_id", "account_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false, foreignKey = @ForeignKey(name = "fk_team_assignments_team"))
    private ScrumTeam team;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_team_assignments_account"))
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScrumRoleType scrumRole;
}
