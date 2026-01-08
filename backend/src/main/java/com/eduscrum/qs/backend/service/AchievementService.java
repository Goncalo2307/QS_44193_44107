package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.model.Achievement;

import java.util.List;

public interface AchievementService {
    Achievement create(Achievement achievement);
    Achievement getById(Long id);
    List<Achievement> listAll();
    Achievement update(Long id, Achievement updated);
    void delete(Long id);
}
