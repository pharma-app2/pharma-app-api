package org.pharma.app.pharmaappapi.security.DTOs;

import org.pharma.app.pharmaappapi.security.models.RoleName;

import java.util.UUID;

public record JwtPayloadPatientDTO(
        UUID id,
        String email,
        RoleName role
) {}
