package com.eduscrum.qs.backend.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CourseRequest(
        @NotBlank String name,
        String description,
        Long teacherAccountId
) {}
