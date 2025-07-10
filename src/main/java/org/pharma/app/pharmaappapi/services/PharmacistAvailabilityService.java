package org.pharma.app.pharmaappapi.services;

import org.pharma.app.pharmaappapi.payloads.pharmacistAvailabilityDTOs.PharmacistAvailabilityDTO;

import java.util.UUID;

public interface PharmacistAvailabilityService {
    void createPharmacistAvailability(UUID userId, PharmacistAvailabilityDTO pharmacistAvailabilityDTO);
}
