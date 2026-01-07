package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.model.ProjectWorkspace;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.ProjectWorkspaceRepository;
import com.eduscrum.qs.backend.service.ProjectWorkspaceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectWorkspaceServiceImpl implements ProjectWorkspaceService {

    private final ProjectWorkspaceRepository repo;

    public ProjectWorkspaceServiceImpl(ProjectWorkspaceRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProjectWorkspace create(ProjectWorkspace project) {
        return repo.save(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectWorkspace getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectWorkspace not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectWorkspace> listAll() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectWorkspace> listByCourse(Long courseId) {
        return repo.findByCourseId(courseId);
    }

    @Override
    public ProjectWorkspace update(Long id, ProjectWorkspace updated) {
        ProjectWorkspace existing = getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.delete(getById(id));
    }
}
