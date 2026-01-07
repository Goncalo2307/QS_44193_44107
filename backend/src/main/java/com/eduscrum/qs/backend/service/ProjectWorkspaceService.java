package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.model.ProjectWorkspace;

import java.util.List;

public interface ProjectWorkspaceService {
    ProjectWorkspace create(ProjectWorkspace project);
    ProjectWorkspace getById(Long id);
    List<ProjectWorkspace> listAll();
    List<ProjectWorkspace> listByCourse(Long courseId);
    ProjectWorkspace update(Long id, ProjectWorkspace updated);
    void delete(Long id);
}
