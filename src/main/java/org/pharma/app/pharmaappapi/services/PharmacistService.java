package org.pharma.app.pharmaappapi.services;

import jakarta.validation.Valid;
import org.pharma.app.pharmaappapi.payloads.pharmacistDTOs.PharmacistDTO;

import java.util.UUID;

public interface PharmacistService {
    PharmacistDTO updatePharmacistProfile(UUID userId, PharmacistDTO pharmacistDTO);
}
