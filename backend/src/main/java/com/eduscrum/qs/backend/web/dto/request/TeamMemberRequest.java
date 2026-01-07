package com.eduscrum.qs.backend.web.dto.request;

import com.eduscrum.qs.backend.domain.enums.ScrumRoleType;
import jakarta.validation.constraints.NotNull;

public record TeamMemberRequest(
        @NotNull Long accountId,
        ScrumRoleType scrumRole
) {}
