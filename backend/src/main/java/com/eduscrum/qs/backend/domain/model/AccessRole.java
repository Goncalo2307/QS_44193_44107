package com.eduscrum.qs.backend.domain.model;

import com.eduscrum.qs.backend.domain.enums.UserRoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "access_roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_access_roles_type", columnNames = "type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRoleType type;
}
