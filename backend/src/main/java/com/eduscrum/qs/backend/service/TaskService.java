package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.model.Task;

import java.util.List;

public interface TaskService {
    Task create(Task task);
    Task getById(Long id);
    List<Task> listAll();
    List<Task> listBySprint(Long sprintId);
    List<Task> listByAssignee(Long assigneeId);
    Task update(Long id, Task updated);
    void delete(Long id);
}
