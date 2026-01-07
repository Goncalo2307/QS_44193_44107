package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> listAll() {
        return accountService.listAll();
    }

    @GetMapping("/{id}")
    public Account getById(@PathVariable Long id) {
        return accountService.getById(id);
    }
}
