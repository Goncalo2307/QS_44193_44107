package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.model.ScrumTeam;

import java.util.List;

public interface ScrumTeamService {
    ScrumTeam create(ScrumTeam team);
    ScrumTeam getById(Long id);
    List<ScrumTeam> listAll();
    List<ScrumTeam> listByProjectWorkspace(Long projectWorkspaceId);
    ScrumTeam update(Long id, ScrumTeam updated);
    void delete(Long id);
}
