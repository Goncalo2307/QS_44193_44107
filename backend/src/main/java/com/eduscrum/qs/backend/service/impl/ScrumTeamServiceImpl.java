package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.model.ScrumTeam;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.ScrumTeamRepository;
import com.eduscrum.qs.backend.service.ScrumTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ScrumTeamServiceImpl implements ScrumTeamService {

    private final ScrumTeamRepository repo;

    public ScrumTeamServiceImpl(ScrumTeamRepository repo) {
        this.repo = repo;
    }

    @Override
    public ScrumTeam create(ScrumTeam team) {
        return repo.save(team);
    }

    @Override
    @Transactional(readOnly = true)
    public ScrumTeam getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ScrumTeam not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScrumTeam> listAll() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScrumTeam> listByProjectWorkspace(Long projectWorkspaceId) {
        return repo.findByProjectWorkspaceId(projectWorkspaceId);
    }

    @Override
    public ScrumTeam update(Long id, ScrumTeam updated) {
        ScrumTeam existing = getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.delete(getById(id));
    }
}
