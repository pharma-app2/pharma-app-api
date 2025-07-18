package org.pharma.app.pharmaappapi.repositories.pharmacistRepository;

import java.util.UUID;

public record ProfileByParamsProjection(
    String pharmacistName,
    UUID id,
    String address,
    Boolean acceptsRemote,
    String ibgeApiCity,
    String ibgeApiState
) {}
