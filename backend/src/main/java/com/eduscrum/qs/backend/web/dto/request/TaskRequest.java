package com.eduscrum.qs.backend.web.dto.request;

import com.eduscrum.qs.backend.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank String title,
        String description,
        TaskStatus status,
        Integer estimatedPoints,
        Long sprintId,
        Long assignedUserId
) {}
