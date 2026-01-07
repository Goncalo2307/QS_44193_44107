package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.AccountAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountAchievementRepository extends JpaRepository<AccountAchievement, Long> {
    List<AccountAchievement> findByAccount_IdOrderByAssignedAtDesc(Long accountId);
}
