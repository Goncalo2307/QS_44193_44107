package com.eduscrum.qs.backend.web.dto.request;

import com.eduscrum.qs.backend.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequest(
        @NotNull Long sprintId,
        @NotBlank String title,
        String description,
        TaskStatus status,
        Long scrumTeamId,
        Long assigneeId
) {}
