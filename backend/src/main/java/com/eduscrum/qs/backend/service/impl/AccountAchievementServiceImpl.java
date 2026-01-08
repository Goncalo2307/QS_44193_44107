package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.model.AccountAchievement;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountAchievementRepository;
import com.eduscrum.qs.backend.service.AccountAchievementService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountAchievementServiceImpl implements AccountAchievementService {

    private final AccountAchievementRepository repo;

    public AccountAchievementServiceImpl(AccountAchievementRepository repo) {
        this.repo = repo;
    }

    @Override
    public AccountAchievement create(AccountAchievement accountAchievement) {
        return repo.save(accountAchievement);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountAchievement getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AccountAchievement not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountAchievement> listAll() {
        return repo.findAll();
    }

    // NOTA: Removi o @Override porque o teu AccountAchievementService não tem este método (ou tem assinatura diferente).
    // Mantém-se aqui porque é útil e já tens repo.findByAccountIdOrderByAssignedAtDesc(...)
    @Transactional(readOnly = true)
    public List<AccountAchievement> listByAccount(Long accountId) {
        return repo.findByAccount_IdOrderByAssignedAtDesc(accountId);
    }

    @Override
    public AccountAchievement update(Long id, AccountAchievement updated) {
        AccountAchievement existing = getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.delete(getById(id));
    }
}
