package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.enums.TaskStatus;
import com.eduscrum.qs.backend.domain.model.*;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.repository.ScrumTeamRepository;
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
    private final ScrumTeamRepository teamRepo;
    private final AccountRepository accountRepo;

    public TaskController(TaskService taskService, SprintRepository sprintRepo, ScrumTeamRepository teamRepo, AccountRepository accountRepo) {
        this.taskService = taskService;
        this.sprintRepo = sprintRepo;
        this.teamRepo = teamRepo;
        this.accountRepo = accountRepo;
    }

    @GetMapping
    public List<Task> listAll(@RequestParam(required = false) Long sprintId,
                              @RequestParam(required = false) Long assigneeId) {
        if (sprintId != null) return taskService.listBySprint(sprintId);
        if (assigneeId != null) return taskService.listByAssignee(assigneeId);
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
        t.setSprint(sprint);
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setStatus(req.status() == null ? TaskStatus.TO_DO : req.status());

        if (req.scrumTeamId() != null) {
            ScrumTeam team = teamRepo.findById(req.scrumTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("ScrumTeam not found: " + req.scrumTeamId()));
            t.setScrumTeam(team);
        }

        if (req.assigneeId() != null) {
            Account assignee = accountRepo.findById(req.assigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.assigneeId()));
            t.setAssignee(assignee);
        }

        return taskService.create(t);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @Valid @RequestBody TaskRequest req) {
        Task existing = taskService.getById(id);

        Sprint sprint = sprintRepo.findById(req.sprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + req.sprintId()));

        existing.setSprint(sprint);
        existing.setTitle(req.title());
        existing.setDescription(req.description());
        if (req.status() != null) existing.setStatus(req.status());

        if (req.scrumTeamId() != null) {
            ScrumTeam team = teamRepo.findById(req.scrumTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("ScrumTeam not found: " + req.scrumTeamId()));
            existing.setScrumTeam(team);
        } else {
            existing.setScrumTeam(null);
        }

        if (req.assigneeId() != null) {
            Account assignee = accountRepo.findById(req.assigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.assigneeId()));
            existing.setAssignee(assignee);
        } else {
            existing.setAssignee(null);
        }

        return taskService.update(id, existing);
    }

    @PatchMapping("/{id}/status")
    public Task updateStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        Task t = taskService.getById(id);
        t.setStatus(status);
        return taskService.update(id, t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
