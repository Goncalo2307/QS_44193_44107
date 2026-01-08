package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.ProjectWorkspace;
import com.eduscrum.qs.backend.domain.model.Sprint;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.ProjectWorkspaceRepository;
import com.eduscrum.qs.backend.service.SprintService;
import com.eduscrum.qs.backend.web.dto.request.SprintRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

    private final SprintService sprintService;
    private final ProjectWorkspaceRepository workspaceRepo;

    public SprintController(SprintService sprintService, ProjectWorkspaceRepository workspaceRepo) {
        this.sprintService = sprintService;
        this.workspaceRepo = workspaceRepo;
    }

    // -----------------------------
    // ENDPOINTS ALINHADOS COM O PROJETO BASE ("antigo")
    // -----------------------------

    @GetMapping("/project/{projectId}")
    public List<Sprint> getSprintsByProject(@PathVariable Long projectId) {
        return sprintService.listByProjectWorkspace(projectId);
    }

    @PostMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Sprint createSprint(@PathVariable Long projectId, @RequestBody Sprint sprint) {
        ProjectWorkspace project = workspaceRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        sprint.setId(null);
        sprint.setProjectWorkspace(project);
        return sprintService.create(sprint);
    }

    @DeleteMapping("/{sprintId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSprint(@PathVariable Long sprintId) {
        sprintService.delete(sprintId);
    }

    // -----------------------------
    // ENDPOINTS ADICIONAIS (mantidos para compatibilidade do teu projeto)
    // -----------------------------

    @GetMapping
    public List<Sprint> listAll(@RequestParam(required = false) Long projectWorkspaceId) {
        if (projectWorkspaceId != null) return sprintService.listByProjectWorkspace(projectWorkspaceId);
        return sprintService.listAll();
    }

    @GetMapping("/{id}")
    public Sprint getById(@PathVariable Long id) {
        return sprintService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sprint create(@Valid @RequestBody SprintRequest req) {
        ProjectWorkspace project = workspaceRepo.findById(req.projectWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + req.projectWorkspaceId()));

        Sprint s = new Sprint();
        s.setName(req.name());
        s.setGoal(req.goal());
        s.setStartDate(req.startDate());
        s.setEndDate(req.endDate());
        s.setProjectWorkspace(project);

        return sprintService.create(s);
    }

    @PutMapping("/{id}")
    public Sprint update(@PathVariable Long id, @Valid @RequestBody SprintRequest req) {
        ProjectWorkspace project = workspaceRepo.findById(req.projectWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + req.projectWorkspaceId()));

        Sprint s = sprintService.getById(id);
        s.setName(req.name());
        s.setGoal(req.goal());
        s.setStartDate(req.startDate());
        s.setEndDate(req.endDate());
        s.setProjectWorkspace(project);

        return sprintService.update(id, s);
    }
}
