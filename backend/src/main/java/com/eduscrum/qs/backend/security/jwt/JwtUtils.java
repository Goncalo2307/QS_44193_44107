package com.eduscrum.qs.backend.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Adapter de compatibilidade com o projeto base (antigo).
 *
 * O teu backend usa {@link JwtTokenProvider}. No projeto base existia normalmente uma classe
 * "JwtUtils". Para manter nomenclatura e imports sem duplicar lógica, esta classe delega para
 * {@link JwtTokenProvider}.
 */
@Component
public class JwtUtils {

    private final JwtTokenProvider tokenProvider;

    public JwtUtils(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public String generateJwtToken(Authentication authentication) {
        return tokenProvider.generateToken(authentication);
    }

    public String getUserNameFromJwtToken(String token) {
        return tokenProvider.getEmailFromToken(token);
    }

    public boolean validateJwtToken(String authToken) {
        return tokenProvider.validateToken(authToken);
    }
}
