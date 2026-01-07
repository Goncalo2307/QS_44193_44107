package com.eduscrum.qs.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_workspaces")
@Getter
@Setter
public class ProjectWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private AcademicCourse course;


    @OneToMany(mappedBy = "projectWorkspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sprint> sprints = new ArrayList<>();


    @OneToMany(mappedBy = "projectWorkspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScrumTeam> teams = new ArrayList<>();
}
