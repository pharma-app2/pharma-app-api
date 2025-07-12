package org.pharma.app.pharmaappapi.services;

import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.AvailabilityParameters;
import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.AvailabilityCreateDTO;

import java.util.Set;
import java.util.UUID;

public interface AvailabilityService {
    void createAvailability(UUID userId, AvailabilityCreateDTO pharmacistAvailabilityDTO);
    Set<AvailabilityCreateDTO> getAvailabilities(UUID pharmacistId, AvailabilityParameters params);
}
