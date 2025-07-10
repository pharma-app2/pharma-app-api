package org.pharma.app.pharmaappapi.security.DTOs.users;

import org.pharma.app.pharmaappapi.security.models.users.RoleName;

import java.util.UUID;

public record JwtPayloadPatientDTO(
        UUID id,
        String email,
        RoleName role
) {}
