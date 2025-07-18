package org.pharma.app.pharmaappapi.services.availabilityService;

import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.AvailabilityCreateDTO;
import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.AvailabilityParameters;
import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.OwnAvailabilityDTO;
import org.pharma.app.pharmaappapi.payloads.pharmacistDTOs.ProfileSearchParamsDTO;

import java.util.Set;
import java.util.UUID;

public interface AvailabilityService {
    void createAvailability(UUID userId, AvailabilityCreateDTO pharmacistAvailabilityDTO);
    Set<AvailabilityCreateDTO> getAvailabilitiesFromPharmacist(UUID pharmacistId, AvailabilityParameters params);
    Set<OwnAvailabilityDTO> getOwnAvailabilities(UUID userId, AvailabilityParameters params);
}
