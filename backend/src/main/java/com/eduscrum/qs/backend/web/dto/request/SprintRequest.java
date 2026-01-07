package com.eduscrum.qs.backend.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SprintRequest(
        @NotBlank String title,
        LocalDate startDate,
        LocalDate endDate,
        @NotNull Long workspaceId
) {}
