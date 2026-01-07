package com.eduscrum.qs.backend.config;

import com.eduscrum.qs.backend.domain.enums.UserRoleType;
import com.eduscrum.qs.backend.domain.model.AccessRole;
import com.eduscrum.qs.backend.repository.AccessRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RoleSeeder implements CommandLineRunner {

    private final AccessRoleRepository roleRepo;

    public RoleSeeder(AccessRoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seed(UserRoleType.ROLE_STUDENT);
        seed(UserRoleType.ROLE_TEACHER);
        seed(UserRoleType.ROLE_ADMIN);
    }

    private void seed(UserRoleType type) {
        roleRepo.findByType(type).orElseGet(() ->
                roleRepo.save(AccessRole.builder().type(type).build())
        );
    }
}
