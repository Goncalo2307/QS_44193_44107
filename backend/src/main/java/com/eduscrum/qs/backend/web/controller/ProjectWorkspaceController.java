package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.AcademicCourse;
import com.eduscrum.qs.backend.domain.model.ProjectWorkspace;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AcademicCourseRepository;
import com.eduscrum.qs.backend.service.ProjectWorkspaceService;
import com.eduscrum.qs.backend.web.dto.request.WorkspaceRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class ProjectWorkspaceController {

    private final ProjectWorkspaceService workspaceService;
    private final AcademicCourseRepository courseRepo;

    public ProjectWorkspaceController(ProjectWorkspaceService workspaceService, AcademicCourseRepository courseRepo) {
        this.workspaceService = workspaceService;
        this.courseRepo = courseRepo;
    }

    @GetMapping
    public List<ProjectWorkspace> listAll(@RequestParam(required = false) Long courseId) {
        if (courseId == null) return workspaceService.listAll();
        return workspaceService.listByCourse(courseId);
    }

    @GetMapping("/{id}")
    public ProjectWorkspace getById(@PathVariable Long id) {
        return workspaceService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectWorkspace create(@Valid @RequestBody WorkspaceRequest req) {
        AcademicCourse course = courseRepo.findById(req.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicCourse not found: " + req.courseId()));

        ProjectWorkspace ws = new ProjectWorkspace();
        ws.setName(req.name());
        ws.setDescription(req.description());
        ws.setCourse(course);

        return workspaceService.create(ws);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ProjectWorkspace update(@PathVariable Long id, @Valid @RequestBody WorkspaceRequest req) {
        ProjectWorkspace existing = workspaceService.getById(id);

        AcademicCourse course = courseRepo.findById(req.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicCourse not found: " + req.courseId()));

        existing.setName(req.name());
        existing.setDescription(req.description());
        existing.setCourse(course);

        return workspaceService.update(id, existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        workspaceService.delete(id);
    }
}
