package com.eduscrum.qs.backend.web.dto.auth;

import java.util.List;

public record JwtResponse(
        String token,
        String type,
        Long id,
        String name,
        String email,
        List<String> roles
) {
    public JwtResponse(String token, Long id, String name, String email, List<String> roles) {
        this(token, "Bearer", id, name, email, roles);
    }
}
