package org.pharma.app.pharmaappapi.services.pharmacistService;

import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.BadRequestException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.healthPlans.HealthPlan;
import org.pharma.app.pharmaappapi.models.locations.PharmacistLocation;
import org.pharma.app.pharmaappapi.payloads.pharmacistDTOs.PharmacistDTO;
import org.pharma.app.pharmaappapi.payloads.pharmacistLocationDTOs.PharmacistLocationDTO;
import org.pharma.app.pharmaappapi.repositories.PharmacistRepository;
import org.pharma.app.pharmaappapi.repositories.healthPlanRepository.HealthPlanRepository;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PharmacistServiceImpl implements PharmacistService {
    private final PharmacistRepository pharmacistRepository;
    private final HealthPlanRepository healthPlanRepository;
    private final ModelMapper modelMapper;

    public PharmacistServiceImpl(
            PharmacistRepository pharmacistRepository,
            HealthPlanRepository healthPlanRepository,
            ModelMapper modelMapper) {
        this.pharmacistRepository = pharmacistRepository;
        this.healthPlanRepository = healthPlanRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PharmacistDTO updatePharmacistProfile(UUID userId, PharmacistDTO pharmacistDTO) {
        Set<PharmacistLocationDTO> pharmacistLocationDTOS = pharmacistDTO.getPharmacistLocations();
        String crf = pharmacistDTO.getCrf();
        Set<UUID> healthPlanIds = pharmacistDTO.getHealthPlanIds();
        Boolean acceptsRemote = pharmacistDTO.getAcceptsRemote();

        Pharmacist pharmacist = pharmacistRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId.toString()));

        pharmacist.setAcceptsRemote(acceptsRemote);
        pharmacist.setCrf(crf);

        Set<PharmacistLocation> newLocations = pharmacistLocationDTOS
                .stream()
                .map(locationDTO -> {
                    PharmacistLocation location = modelMapper.map(locationDTO, PharmacistLocation.class);
                    location.setPharmacist(pharmacist);
                    return location;
                })
                .collect(Collectors.toSet());

        Set<PharmacistLocation> existingLocations = pharmacist.getPharmacistLocations();
        existingLocations.clear();
        existingLocations.addAll(newLocations);

        Set<HealthPlan> newHealthPlans = new HashSet<>(healthPlanRepository.findAllById(healthPlanIds));
        if (newHealthPlans.size() != healthPlanIds.size()) {
            throw new BadRequestException("Um ou mais ids de plano de saúde são inválidos");
        }

        pharmacist.getHealthPlans().clear();
        pharmacist.getHealthPlans().addAll(newHealthPlans);
        for (HealthPlan healthPlan: newHealthPlans) {
            healthPlan.getPharmacists().add(pharmacist);
        }

        Pharmacist savedPharmacist = pharmacistRepository.save(pharmacist);

        return modelMapper.map(savedPharmacist, PharmacistDTO.class);
    }
}
