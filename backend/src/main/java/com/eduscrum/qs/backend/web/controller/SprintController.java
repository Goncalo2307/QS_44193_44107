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

    @GetMapping
    public List<Sprint> listAll(@RequestParam(required = false) Long workspaceId) {
        if (workspaceId == null) return sprintService.listAll();
        return sprintService.listByProjectWorkspace(workspaceId);
    }

    @GetMapping("/{id}")
    public Sprint getById(@PathVariable Long id) {
        return sprintService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sprint create(@Valid @RequestBody SprintRequest req) {
        ProjectWorkspace ws = workspaceRepo.findById(req.workspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectWorkspace not found: " + req.workspaceId()));

        Sprint s = new Sprint();
        s.setTitle(req.title());
        s.setStartDate(req.startDate());
        s.setEndDate(req.endDate());
        s.setProjectWorkspace(ws);

        return sprintService.create(s);
    }

    @PutMapping("/{id}")
    public Sprint update(@PathVariable Long id, @Valid @RequestBody SprintRequest req) {
        Sprint s = sprintService.getById(id);

        ProjectWorkspace ws = workspaceRepo.findById(req.workspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectWorkspace not found: " + req.workspaceId()));

        s.setTitle(req.title());
        s.setStartDate(req.startDate());
        s.setEndDate(req.endDate());
        s.setProjectWorkspace(ws);

        return sprintService.update(id, s);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        sprintService.delete(id);
    }
}
