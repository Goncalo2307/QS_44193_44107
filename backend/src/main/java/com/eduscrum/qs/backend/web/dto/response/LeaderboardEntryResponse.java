package com.eduscrum.qs.backend.web.dto.response;

public record LeaderboardEntryResponse(
        Long accountId,
        String name,
        long totalAchievements,
        int totalPoints
) {}
