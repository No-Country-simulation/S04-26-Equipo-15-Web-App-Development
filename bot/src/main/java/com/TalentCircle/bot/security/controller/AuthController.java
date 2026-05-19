package com.TalentCircle.bot.security.controller;

import com.TalentCircle.bot.security.dto.AuthResponse;
import com.TalentCircle.bot.security.dto.LoginRequest;
import com.TalentCircle.bot.security.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Auth", description = "Autenticación y JWT")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login de usuario")
    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ) {

        String token = authService.login(request);

        return new AuthResponse(token);
    }
}
