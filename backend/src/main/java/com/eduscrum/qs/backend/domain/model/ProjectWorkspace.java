package com.eduscrum.qs.backend.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_workspaces")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProjectWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    // Muitos Projetos pertencem a 1 Curso
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore // evita loops (o curso pode ter lista de projetos)
    private AcademicCourse course;

    // Sprints associadas ao projeto (apenas para cascata); não expor por defeito.
    @JsonIgnore
    @OneToMany(mappedBy = "projectWorkspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sprint> sprints = new ArrayList<>();

    // Regra do enunciado / projeto base: um projeto tem UMA equipa.
    @OneToOne(mappedBy = "projectWorkspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private ScrumTeam team;
}
