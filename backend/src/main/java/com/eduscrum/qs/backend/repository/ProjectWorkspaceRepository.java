package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.ProjectWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectWorkspaceRepository extends JpaRepository<ProjectWorkspace, Long> {
    List<ProjectWorkspace> findByCourseId(Long courseId);
}
