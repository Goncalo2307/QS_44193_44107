package com.eduscrum.qs.backend.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkspaceRequest(
        @NotBlank String name,
        String description,
        @NotNull Long courseId
) {}
