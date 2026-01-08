package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.security.services.AuthService;
import com.eduscrum.qs.backend.web.dto.auth.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    // Mantido para compatibilidade do teu projeto
    @PostMapping("/register")
    public ResponseEntity<Account> register(@Valid @RequestBody SignupRequest req) {
        return ResponseEntity.ok(auth.register(req));
    }

    // Alias do projeto base (antigo)
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest req) {
        auth.register(req);
        return ResponseEntity.ok(new MessageResponse("Utilizador registado com sucesso!"));
    }

    // Mantido para compatibilidade do teu projeto
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(auth.login(req));
    }

    // Alias do projeto base (antigo)
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> signin(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(auth.login(req));
    }
}
