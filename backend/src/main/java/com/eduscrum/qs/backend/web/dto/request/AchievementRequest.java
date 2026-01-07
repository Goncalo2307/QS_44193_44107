package com.eduscrum.qs.backend.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AchievementRequest(
        @NotBlank String name,
        String description,
        @NotNull Integer points
) {}
