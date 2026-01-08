package com.eduscrum.qs.backend.web.dto.response;

public record TeamLeaderboardResponse(
        String teamName,
        String projectName,
        Long totalPoints
) {}
