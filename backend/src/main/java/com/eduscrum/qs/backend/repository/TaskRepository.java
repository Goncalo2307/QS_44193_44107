package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findBySprintId(Long sprintId);
    List<Task> findByAssigneeId(Long assigneeId);
}
