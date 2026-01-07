package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.enums.UserRoleType;
import com.eduscrum.qs.backend.domain.model.AccessRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessRoleRepository extends JpaRepository<AccessRole, Long> {
    Optional<AccessRole> findByType(UserRoleType type);
    boolean existsByType(UserRoleType type);
}
