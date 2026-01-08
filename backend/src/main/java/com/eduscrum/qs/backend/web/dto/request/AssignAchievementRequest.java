package com.eduscrum.qs.backend.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de compatibilidade com o projeto base (antigo).
 *
 * No projeto base: AssignBadgeRequest { studentEmail, badgeId }
 * Aqui: Achievement ("badge") + AccountAchievement.
 */
public record AssignAchievementRequest(
        @NotBlank String studentEmail,
        @NotNull Long achievementId
) {}
