package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.model.TeamAssignment;
import com.eduscrum.qs.backend.exception.ConflictException;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.TeamAssignmentRepository;
import com.eduscrum.qs.backend.service.TeamAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeamAssignmentServiceImpl implements TeamAssignmentService {

    private final TeamAssignmentRepository repo;

    public TeamAssignmentServiceImpl(TeamAssignmentRepository repo) {
        this.repo = repo;
    }

    @Override
    public TeamAssignment create(TeamAssignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("assignment cannot be null");
        }
        if (assignment.getTeam() == null || assignment.getTeam().getId() == null) {
            throw new IllegalArgumentException("assignment.team.id is required");
        }
        if (assignment.getAccount() == null || assignment.getAccount().getId() == null) {
            throw new IllegalArgumentException("assignment.account.id is required");
        }

        Long teamId = assignment.getTeam().getId();
        Long accountId = assignment.getAccount().getId();

        if (repo.findByTeamIdAndAccountId(teamId, accountId).isPresent()) {
            throw new ConflictException("Account already assigned to this team.");
        }

        return repo.save(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamAssignment getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeamAssignment not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamAssignment> listAll() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamAssignment> listByTeam(Long teamId) {
        return repo.findByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamAssignment> listByAccount(Long accountId) {
        return repo.findByAccountId(accountId);
    }

    @Override
    public void delete(Long id) {
        repo.delete(getById(id));
    }
}
