package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.ScrumTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrumTeamRepository extends JpaRepository<ScrumTeam, Long> {
    List<ScrumTeam> findByProjectWorkspaceId(Long projectWorkspaceId);

    Optional<ScrumTeam> findFirstByProjectWorkspaceId(Long projectWorkspaceId);

    boolean existsByProjectWorkspaceId(Long projectWorkspaceId);
}
