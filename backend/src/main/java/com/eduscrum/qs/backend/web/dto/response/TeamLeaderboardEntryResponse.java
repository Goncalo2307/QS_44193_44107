package com.eduscrum.qs.backend.web.dto.response;

import com.eduscrum.qs.backend.domain.enums.ScrumRoleType;

public record TeamLeaderboardEntryResponse(
        Long teamId,
        Long accountId,
        String name,
        ScrumRoleType scrumRole,
        long totalAchievements,
        int totalPoints
) {}
