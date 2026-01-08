package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.enums.TaskStatus;
import com.eduscrum.qs.backend.domain.model.Task;

import java.util.List;

public interface TaskService {
    Task create(Task task);
    Task getById(Long id);
    List<Task> listAll();
    List<Task> listBySprint(Long sprintId);
    List<Task> listByAssignedUser(Long userId);
    Task update(Long id, Task updated);
    Task updateStatus(Long taskId, TaskStatus newStatus);
    Task assignTaskToUser(Long taskId, Long userId);
    void delete(Long id);
}
