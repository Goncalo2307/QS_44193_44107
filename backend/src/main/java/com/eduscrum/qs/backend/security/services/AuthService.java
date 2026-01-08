package com.eduscrum.qs.backend.security.services;

import com.eduscrum.qs.backend.domain.enums.UserRoleType;
import com.eduscrum.qs.backend.domain.model.AccessRole;
import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.exception.ConflictException;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccessRoleRepository;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.security.jwt.JwtUtils;
import com.eduscrum.qs.backend.web.dto.auth.JwtResponse;
import com.eduscrum.qs.backend.web.dto.auth.LoginRequest;
import com.eduscrum.qs.backend.web.dto.auth.SignupRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class AuthService {

    private final AccountRepository accountRepo;
    private final AccessRoleRepository roleRepo;
    private final PasswordEncoder encoder;

    // Para login
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(
            AccountRepository accountRepo,
            AccessRoleRepository roleRepo,
            PasswordEncoder encoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils
    ) {
        this.accountRepo = accountRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public Account register(SignupRequest req) {

        if (accountRepo.existsByEmail(req.email())) {
            throw new ConflictException("Email already in use.");
        }

        UserRoleType roleType = parseRole(req.role());

        AccessRole role = roleRepo.findByType(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleType));

        Account acc = Account.builder()
                .name(req.name())
                .email(req.email())
                .password(encoder.encode(req.password()))
                .roles(Set.of(role))
                .build();

        return accountRepo.save(acc);
    }

    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest req) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        Object principal = authentication.getPrincipal();

        if (principal instanceof AccountDetails details) {
            var roles = details.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .toList();

            return new JwtResponse(
                    jwt,
                    details.getId(),
                    details.getName(),
                    details.getUsername(),
                    roles
            );
        }

        // Fallback (não esperado) - mantém token e campos nulos
        return new JwtResponse(jwt, null, null, authentication.getName(), List.of());
    }

    private UserRoleType parseRole(String raw) {
        if (raw == null || raw.isBlank()) return UserRoleType.ROLE_STUDENT;
        try { return UserRoleType.valueOf(raw.trim().toUpperCase()); }
        catch (Exception ignored) { return UserRoleType.ROLE_STUDENT; }
    }
}
