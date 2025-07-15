package org.pharma.app.pharmaappapi.security.DTOs.users;

import org.springframework.http.ResponseCookie;

import java.util.UUID;

public record LoginResponse(
        UUID id,
        String email,
        String role,
        String fullName,
        ResponseCookie jwtCookie
) {}
