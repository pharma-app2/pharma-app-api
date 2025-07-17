package org.pharma.app.pharmaappapi.repositories.pharmacistRepository;

public record ProfileByParamsProjection(
    String pharmacistName,
    String address,
    Boolean acceptsRemote,
    String ibgeApiCity,
    String ibgeApiState
) {}
