package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.model.Sprint;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.SprintRepository;
import com.eduscrum.qs.backend.service.SprintService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SprintServiceImpl implements SprintService {

    private final SprintRepository repo;

    public SprintServiceImpl(SprintRepository repo) {
        this.repo = repo;
    }

    @Override
    public Sprint create(Sprint sprint) {
        return repo.save(sprint);
    }

    @Override
    @Transactional(readOnly = true)
    public Sprint getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sprint> listAll() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sprint> listByProjectWorkspace(Long projectWorkspaceId) {
        return repo.findByProjectWorkspaceId(projectWorkspaceId);
    }

    @Override
    public Sprint update(Long id, Sprint updated) {
        Sprint existing = getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.delete(getById(id));
    }
}
