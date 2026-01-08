package com.eduscrum.qs.backend.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "scrum_teams",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_scrum_teams_workspace", columnNames = "workspace_id")
        }
)
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ScrumTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Um Projeto só pode ter UMA Equipa.
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    @JsonIgnore // evita loops gigantes ao listar a equipa
    private ProjectWorkspace projectWorkspace;

    // Membros da equipa (apenas via endpoint dedicado)
    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamAssignment> members = new ArrayList<>();
}
