package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.enums.TaskStatus;
import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.domain.model.Task;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.repository.TaskRepository;
import com.eduscrum.qs.backend.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepo;
    private final AccountRepository accountRepo;

    public TaskServiceImpl(TaskRepository taskRepo, AccountRepository accountRepo) {
        this.taskRepo = taskRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    public Task create(Task task) {
        return taskRepo.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Task getById(Long id) {
        return taskRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> listAll() {
        return taskRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> listBySprint(Long sprintId) {
        return taskRepo.findBySprintId(sprintId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> listByAssignedUser(Long userId) {
        return taskRepo.findByAssignedUserId(userId);
    }

    @Override
    public Task update(Long id, Task updated) {
        Task existing = getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        return taskRepo.save(existing);
    }

    @Override
    public Task updateStatus(Long taskId, TaskStatus newStatus) {
        Task task = getById(taskId);
        task.setStatus(newStatus);
        return taskRepo.save(task);
    }

    @Override
    public Task assignTaskToUser(Long taskId, Long userId) {
        Task task = getById(taskId);
        Account user = accountRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + userId));
        task.setAssignedUser(user);
        return taskRepo.save(task);
    }

    @Override
    public void delete(Long id) {
        taskRepo.delete(getById(id));
    }
}
