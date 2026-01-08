package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.enums.TaskStatus;
import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.domain.model.Sprint;
import com.eduscrum.qs.backend.domain.model.Task;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.repository.SprintRepository;
import com.eduscrum.qs.backend.service.TaskService;
import com.eduscrum.qs.backend.web.dto.request.TaskRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final SprintRepository sprintRepo;
    private final AccountRepository accountRepo;

    public TaskController(TaskService taskService, SprintRepository sprintRepo, AccountRepository accountRepo) {
        this.taskService = taskService;
        this.sprintRepo = sprintRepo;
        this.accountRepo = accountRepo;
    }

    // -----------------------------
    // ENDPOINTS ALINHADOS COM O PROJETO BASE ("antigo")
    // -----------------------------

    @GetMapping("/sprint/{sprintId}")
    public List<Task> getTasksBySprint(@PathVariable Long sprintId) {
        return taskService.listBySprint(sprintId);
    }

    @PostMapping("/sprint/{sprintId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@PathVariable Long sprintId, @RequestBody Task task) {
        Sprint sprint = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + sprintId));

        task.setId(null);
        task.setSprint(sprint);
        return taskService.create(task);
    }

    @PutMapping("/status/{taskId}")
    public Task updateTaskStatus(@PathVariable Long taskId, @RequestParam TaskStatus newStatus) {
        return taskService.updateStatus(taskId, newStatus);
    }

    @PutMapping("/assign/{taskId}/user/{userId}")
    public Task assignTaskToUser(@PathVariable Long taskId, @PathVariable Long userId) {
        return taskService.assignTaskToUser(taskId, userId);
    }

    // -----------------------------
    // ENDPOINTS ADICIONAIS (mantidos para compatibilidade do teu projeto)
    // -----------------------------

    @GetMapping
    public List<Task> listAll(
            @RequestParam(required = false) Long sprintId,
            @RequestParam(required = false) Long assignedUserId
    ) {
        if (sprintId != null) return taskService.listBySprint(sprintId);
        if (assignedUserId != null) return taskService.listByAssignedUser(assignedUserId);
        return taskService.listAll();
    }

    @GetMapping("/{id}")
    public Task getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody TaskRequest req) {
        Sprint sprint = sprintRepo.findById(req.sprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + req.sprintId()));

        Task t = new Task();
        t.setTitle(req.title());
        t.setDescription(req.description());
        if (req.status() != null) t.setStatus(req.status());
        t.setEstimatedPoints(req.estimatedPoints());
        t.setSprint(sprint);

        if (req.assignedUserId() != null) {
            Account user = accountRepo.findById(req.assignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.assignedUserId()));
            t.setAssignedUser(user);
        }

        return taskService.create(t);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @Valid @RequestBody TaskRequest req) {
        Task t = taskService.getById(id);

        Sprint sprint = sprintRepo.findById(req.sprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + req.sprintId()));

        t.setTitle(req.title());
        t.setDescription(req.description());
        if (req.status() != null) t.setStatus(req.status());
        t.setEstimatedPoints(req.estimatedPoints());
        t.setSprint(sprint);

        if (req.assignedUserId() != null) {
            Account user = accountRepo.findById(req.assignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.assignedUserId()));
            t.setAssignedUser(user);
        } else {
            t.setAssignedUser(null);
        }

        return taskService.update(id, t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
