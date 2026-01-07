package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.model.AccountAchievement;
import java.util.List;

public interface AccountAchievementService {
    AccountAchievement create(AccountAchievement accountAchievement);

    AccountAchievement getById(Long id);

    List<AccountAchievement> listAll();

    List<AccountAchievement> listByAccount(Long accountId);

    AccountAchievement update(Long id, AccountAchievement updated);

    void delete(Long id);
}
