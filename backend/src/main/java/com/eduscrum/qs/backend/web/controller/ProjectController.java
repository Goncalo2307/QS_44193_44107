package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.AcademicCourse;
import com.eduscrum.qs.backend.domain.model.ProjectWorkspace;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AcademicCourseRepository;
import com.eduscrum.qs.backend.service.ProjectWorkspaceService;
import com.eduscrum.qs.backend.web.dto.request.ProjectRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectWorkspaceService workspaceService;
    private final AcademicCourseRepository courseRepo;

    public ProjectController(ProjectWorkspaceService workspaceService, AcademicCourseRepository courseRepo) {
        this.workspaceService = workspaceService;
        this.courseRepo = courseRepo;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectWorkspace createProject(@Valid @RequestBody ProjectRequest req) {
        AcademicCourse course = courseRepo.findById(req.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + req.courseId()));

        ProjectWorkspace project = new ProjectWorkspace();
        project.setName(req.name());
        project.setDescription(req.description());
        project.setCourse(course);

        return workspaceService.create(project);
    }

    @GetMapping("/course/{courseId}")
    public List<ProjectWorkspace> getProjectsByCourse(@PathVariable Long courseId) {
        return workspaceService.listByCourse(courseId);
    }

    @GetMapping("/{id}")
    public ProjectWorkspace getById(@PathVariable Long id) {
        return workspaceService.getById(id);
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long projectId) {
        workspaceService.delete(projectId);
    }
}
