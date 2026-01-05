package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.TeamAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamAssignmentRepository extends JpaRepository<TeamAssignment, Long> {
    List<TeamAssignment> findByScrumTeamId(Long scrumTeamId);
    List<TeamAssignment> findByAccountId(Long accountId);
    Optional<TeamAssignment> findByScrumTeamIdAndAccountId(Long scrumTeamId, Long accountId);
}
