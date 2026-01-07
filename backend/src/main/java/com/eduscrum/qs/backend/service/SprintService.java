package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.model.Sprint;

import java.util.List;

public interface SprintService {
    Sprint create(Sprint sprint);
    Sprint getById(Long id);
    List<Sprint> listAll();
    List<Sprint> listByProjectWorkspace(Long projectWorkspaceId);
    Sprint update(Long id, Sprint updated);
    void delete(Long id);
}
