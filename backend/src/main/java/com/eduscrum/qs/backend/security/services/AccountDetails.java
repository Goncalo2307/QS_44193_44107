package com.eduscrum.qs.backend.security.services;

import com.eduscrum.qs.backend.domain.model.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AccountDetails implements UserDetails {

    private final Account account;

    public AccountDetails(Account account) {
        this.account = account;
    }

    public Long getId() { return account.getId(); }

    public String getName() { return account.getName(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getRoles().stream()
                .map(r -> r.getType().name())          // UserRoleType -> "ROLE_TEACHER"
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override public String getPassword() { return account.getPassword(); }

    @Override public String getUsername() { return account.getEmail(); }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
