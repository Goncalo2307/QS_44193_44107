package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.repository.AccountRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint de compatibilidade com o projeto base (antigo):
 * GET /api/test/users
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final AccountRepository accountRepo;

    public TestController(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @GetMapping("/users")
    public List<Account> getAllUsers() {
        return accountRepo.findAll();
    }
}
