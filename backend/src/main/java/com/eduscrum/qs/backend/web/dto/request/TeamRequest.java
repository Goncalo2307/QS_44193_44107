package com.eduscrum.qs.backend.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeamRequest(
        @NotBlank String name,
        @NotNull Long workspaceId
) {}
