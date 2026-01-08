package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
