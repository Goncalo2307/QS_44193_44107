package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.model.Achievement;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AchievementRepository;
import com.eduscrum.qs.backend.service.AchievementService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository repo;

    public AchievementServiceImpl(AchievementRepository repo) {
        this.repo = repo;
    }

    @Override
    public Achievement create(Achievement achievement) {
        return repo.save(achievement);
    }

    @Override
    @Transactional(readOnly = true)
    public Achievement getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Achievement> listAll() {
        return repo.findAll();
    }

    @Override
    public Achievement update(Long id, Achievement updated) {
        Achievement existing = getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.delete(getById(id));
    }
}
