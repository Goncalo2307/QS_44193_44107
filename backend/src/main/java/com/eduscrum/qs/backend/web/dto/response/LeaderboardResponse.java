package com.eduscrum.qs.backend.web.dto.response;

public record LeaderboardResponse(
        String studentName,
        Long totalPoints
) {}
