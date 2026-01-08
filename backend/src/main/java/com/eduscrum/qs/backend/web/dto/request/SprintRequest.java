package com.eduscrum.qs.backend.web.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record SprintRequest(
        @NotBlank String name,
        String goal,
        LocalDate startDate,
        LocalDate endDate,
        Long projectWorkspaceId
) {}
