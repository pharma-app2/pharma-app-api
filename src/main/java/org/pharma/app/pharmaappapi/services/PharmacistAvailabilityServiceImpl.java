package org.pharma.app.pharmaappapi.services;

import org.pharma.app.pharmaappapi.payloads.pharmacistAvailabilityDTOs.PharmacistAvailabilityDTO;
import org.pharma.app.pharmaappapi.repositories.PharmacistAvailabilityRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PharmacistAvailabilityServiceImpl implements PharmacistAvailabilityService {
    private final PharmacistAvailabilityRepository pharmacistAvailabilityRepository;

    public PharmacistAvailabilityServiceImpl(PharmacistAvailabilityRepository pharmacistAvailabilityRepository) {
        this.pharmacistAvailabilityRepository = pharmacistAvailabilityRepository;
    }

    @Override
    public void createPharmacistAvailability(UUID userId, PharmacistAvailabilityDTO pharmacistAvailabilityDTO) {

    }
}
