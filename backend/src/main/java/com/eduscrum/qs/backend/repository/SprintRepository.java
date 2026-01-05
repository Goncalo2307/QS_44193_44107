package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProjectWorkspaceId(Long projectWorkspaceId);
}
