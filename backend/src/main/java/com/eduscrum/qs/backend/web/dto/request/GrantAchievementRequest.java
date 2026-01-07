package com.eduscrum.qs.backend.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record GrantAchievementRequest(
        @NotNull Long accountId,
        @NotNull Long achievementId
) {}
