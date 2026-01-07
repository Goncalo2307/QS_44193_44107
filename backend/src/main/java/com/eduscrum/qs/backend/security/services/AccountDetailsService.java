package com.eduscrum.qs.backend.security.services;

import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.repository.AccountRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepo;

    public AccountDetailsService(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account acc = accountRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found with email: " + email));

        return new AccountDetails(acc);
    }
}
