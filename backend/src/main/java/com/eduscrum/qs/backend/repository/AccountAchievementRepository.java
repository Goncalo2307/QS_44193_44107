package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.AccountAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountAchievementRepository extends JpaRepository<AccountAchievement, Long> {
    List<AccountAchievement> findByAccountIdOrderByAwardedAtDesc(Long accountId);
    boolean existsByAccountIdAndAchievementId(Long accountId, Long achievementId);
}
