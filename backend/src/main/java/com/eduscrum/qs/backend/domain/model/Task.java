package com.eduscrum.qs.backend.domain.model;

import com.eduscrum.qs.backend.domain.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TO_DO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;


    @ManyToOne
    @JoinColumn(name = "scrum_team_id")
    private ScrumTeam scrumTeam;


    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private Account assignee;
}
