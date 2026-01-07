package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.model.Task;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.TaskRepository;
import com.eduscrum.qs.backend.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repo;

    public TaskServiceImpl(TaskRepository repo) {
        this.repo = repo;
    }

    @Override
    public Task create(Task task) {
        return repo.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Task getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> listAll() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> listBySprint(Long sprintId) {
        return repo.findBySprintId(sprintId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> listByAssignee(Long assigneeId) {
        return repo.findByAssigneeId(assigneeId);
    }

    @Override
    public Task update(Long id, Task updated) {
        Task existing = getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.delete(getById(id));
    }
}
