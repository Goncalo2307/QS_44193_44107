package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.model.TeamAssignment;

import java.util.List;

public interface TeamAssignmentService {
    TeamAssignment create(TeamAssignment assignment);
    TeamAssignment getById(Long id);
    List<TeamAssignment> listAll();
    List<TeamAssignment> listByTeam(Long scrumTeamId);
    List<TeamAssignment> listByAccount(Long accountId);
    void delete(Long id);
}
