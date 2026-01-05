package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.ScrumTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrumTeamRepository extends JpaRepository<ScrumTeam, Long> {
    List<ScrumTeam> findByProjectWorkspaceId(Long projectWorkspaceId);
}
