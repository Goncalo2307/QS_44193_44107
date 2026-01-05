package com.eduscrum.qs.backend.domain.model;

import com.eduscrum.qs.backend.domain.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TaskStatus status = TaskStatus.TO_DO;

    @Column(name = "estimated_points")
    private Integer estimatedPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_account_id", foreignKey = @ForeignKey(name = "fk_tasks_assigned_account"))
    private Account assignedAccount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tasks_sprint"))
    private Sprint sprint;
}
