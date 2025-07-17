package org.pharma.app.pharmaappapi.services.pharmacistService;

import org.pharma.app.pharmaappapi.payloads.pharmacistDTOs.PharmacistDTO;
import org.pharma.app.pharmaappapi.payloads.pharmacistDTOs.ProfileSearchParamsDTO;

import java.util.Set;
import java.util.UUID;

public interface PharmacistService {
    PharmacistDTO updatePharmacistProfile(UUID userId, PharmacistDTO pharmacistDTO);
    PharmacistDTO getPharmacistProfile(UUID userId);
    Set<ProfileSearchParamsDTO> getProfilesByParams(ProfileSearchParamsDTO params);
}
