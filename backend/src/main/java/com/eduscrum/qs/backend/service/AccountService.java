package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.model.Account;

import java.util.List;

public interface AccountService {
    Account create(Account account);
    Account getById(Long id);
    Account getByEmail(String email);
    List<Account> listAll();
    Account update(Long id, Account updated);
    void delete(Long id);
}
