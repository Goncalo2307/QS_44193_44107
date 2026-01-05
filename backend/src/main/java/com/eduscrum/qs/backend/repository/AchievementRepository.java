package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByCreatedById(Long teacherId);
}
