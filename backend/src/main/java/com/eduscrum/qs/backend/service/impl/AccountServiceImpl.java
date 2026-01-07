package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.exception.ConflictException;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.service.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repo;

    public AccountServiceImpl(AccountRepository repo) {
        this.repo = repo;
    }

    @Override
    public Account create(Account account) {
        // regra típica do outro projeto: email único
        if (account.getEmail() != null && repo.existsByEmail(account.getEmail())) {
            throw new ConflictException("Email already exists.");
        }
        return repo.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Account getByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> listAll() {
        return repo.findAll();
    }

    @Override
    public Account update(Long id, Account updated) {
        Account existing = getById(id);

        // copia “genérica” sem mexer no id (mantém estrutura sem te obrigar a definir campos aqui)
        BeanUtils.copyProperties(updated, existing, "id");
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        Account existing = getById(id);
        repo.delete(existing);
    }
}
