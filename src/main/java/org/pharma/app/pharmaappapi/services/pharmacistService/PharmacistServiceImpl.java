package org.pharma.app.pharmaappapi.services.pharmacistService;

import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.BadRequestException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentModality;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentModalityName;
import org.pharma.app.pharmaappapi.models.healthPlans.HealthPlan;
import org.pharma.app.pharmaappapi.models.locations.PharmacistLocation;
import org.pharma.app.pharmaappapi.payloads.pharmacistDTOs.PharmacistDTO;
import org.pharma.app.pharmaappapi.payloads.pharmacistDTOs.ProfileSearchParamsDTO;
import org.pharma.app.pharmaappapi.payloads.pharmacistLocationDTOs.PharmacistLocationDTO;
import org.pharma.app.pharmaappapi.repositories.appointmentModalityRepository.AppointmentModalityRepository;
import org.pharma.app.pharmaappapi.repositories.healthPlanRepository.HealthPlanRepository;
import org.pharma.app.pharmaappapi.repositories.pharmacistRepository.PharmacistProfileFlatProjection;
import org.pharma.app.pharmaappapi.repositories.pharmacistRepository.ProfileByParamsProjection;
import org.pharma.app.pharmaappapi.repositories.pharmacistRepository.ProfileRepository;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PharmacistServiceImpl implements PharmacistService {
    private final ProfileRepository pharmacistRepository;
    private final HealthPlanRepository healthPlanRepository;
    private final AppointmentModalityRepository appointmentModalityRepository;
    private final ModelMapper modelMapper;

    public PharmacistServiceImpl(
            ProfileRepository pharmacistRepository,
            HealthPlanRepository healthPlanRepository,
            AppointmentModalityRepository appointmentModalityRepository,
            ModelMapper modelMapper) {
        this.pharmacistRepository = pharmacistRepository;
        this.healthPlanRepository = healthPlanRepository;
        this.appointmentModalityRepository = appointmentModalityRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PharmacistDTO getPharmacistProfile(UUID userId) {
        Set<PharmacistProfileFlatProjection> pharmacistProfile = pharmacistRepository.findPharmacistProfile(userId);
        return mapToNested(pharmacistProfile);
    }

    @Override
    public PharmacistDTO updatePharmacistProfile(UUID userId, PharmacistDTO pharmacistDTO) {
        Set<PharmacistLocationDTO> pharmacistLocationDTOS = pharmacistDTO.getPharmacistLocations();
        String crf = pharmacistDTO.getCrf();
        String email = pharmacistDTO.getEmail();
        String fullName = pharmacistDTO.getFullName();

        Set<String> modalities = pharmacistDTO.getModalities();

        Set<String> healthPlanNames = pharmacistDTO.getHealthPlanNames();
        Boolean acceptsRemote = pharmacistDTO.getAcceptsRemote();

        Pharmacist pharmacist = pharmacistRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId.toString()));

        pharmacist.setAcceptsRemote(acceptsRemote);
        pharmacist.getUser().setEmail(email);
        pharmacist.getUser().setFullName(fullName);
        pharmacist.setCrf(crf);

        Set<AppointmentModalityName> names = modalities
                .stream()
                .map(AppointmentModalityName::valueOf)
                .collect(Collectors.toSet());
        Set<AppointmentModality> dbModalities = appointmentModalityRepository.findAllByNameIn(names);

        if (dbModalities.size() != modalities.size()) {
            throw new BadRequestException("Um ou mais nomes de modalidades de consulta são inválidos");
        }

        pharmacist.getAvailableModalities().clear();
        pharmacist.getAvailableModalities().addAll(dbModalities);

        if (!modalities.contains(AppointmentModalityName.TELECONSULTA.name())) {
            pharmacist.setAcceptsRemote(false); // ensures that pharmacist doesn't mistakenly forget to set acceptsRemote as false
        }

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

        Set<HealthPlan> dbHealthPlans = healthPlanRepository.findAllByPlanNameIn(healthPlanNames);
        if (dbHealthPlans.size() != healthPlanNames.size()) {
            throw new BadRequestException("Um ou mais nomes de plano de saúde são inválidos");
        }

        pharmacist.getHealthPlans().clear();
        pharmacist.getHealthPlans().addAll(dbHealthPlans);
        for (HealthPlan healthPlan: dbHealthPlans) {
            healthPlan.getPharmacists().add(pharmacist);
        }

        Pharmacist savedProfile = pharmacistRepository.save(pharmacist);
        PharmacistDTO savedProfileDTO = modelMapper.map(savedProfile, PharmacistDTO.class);

        savedProfileDTO.setHealthPlanNames(healthPlanNames);
        savedProfileDTO.setModalities(modalities);
        savedProfileDTO.setEmail(email);
        savedProfileDTO.setFullName(fullName);

        return savedProfileDTO;
    }

    @Override
    public Set<ProfileSearchParamsDTO> getProfilesByParams(ProfileSearchParamsDTO params) {
        String pharmacistName = params.getPharmacistName();
        String ibgeApiCity = params.getIbgeApiCity();
        String ibgeApiState = params.getIbgeApiState();
        boolean acceptsRemote = Boolean.parseBoolean(params.getAcceptsRemote());

        Set<ProfileByParamsProjection> availabilities = pharmacistRepository
                .findProfilesByParams(pharmacistName, ibgeApiCity, ibgeApiState, acceptsRemote);

        Set<ProfileSearchParamsDTO> dtos = new HashSet<>();

        availabilities.forEach(availability -> {
            ProfileSearchParamsDTO availabilitySearchParamsDTO = new ProfileSearchParamsDTO();

            availabilitySearchParamsDTO.setPharmacistName(availability.pharmacistName());
            availabilitySearchParamsDTO.setId(availability.id().toString());
            availabilitySearchParamsDTO.setIbgeApiCity(availability.ibgeApiCity());
            availabilitySearchParamsDTO.setIbgeApiState(availability.ibgeApiState());
            availabilitySearchParamsDTO.setAddress(availability.address());
            availabilitySearchParamsDTO.setAcceptsRemote(availability.acceptsRemote().toString());

            dtos.add(availabilitySearchParamsDTO);
        });

        return dtos;
    }

    private PharmacistDTO mapToNested(Set<PharmacistProfileFlatProjection> flatResults) {
        Set<PharmacistLocationDTO> locationDTOs = new HashSet<>();
        Set<String> modalities = new HashSet<>();
        Set<String> healthPlanNames = new HashSet<>();

        flatResults.forEach(projection -> {
            PharmacistLocationDTO pharmacistLocationDTO = new PharmacistLocationDTO();

            pharmacistLocationDTO.setAddress(projection.getAddress());
            pharmacistLocationDTO.setPhone1(projection.getPhone1());
            pharmacistLocationDTO.setPhone2(projection.getPhone2());
            pharmacistLocationDTO.setPhone3(projection.getPhone3());
            pharmacistLocationDTO.setIbgeApiIdentifierCity(projection.getIbgeApiCityId());
            pharmacistLocationDTO.setIbgeApiCity(projection.getIbgeApiCity());
            pharmacistLocationDTO.setIbgeApiState(projection.getIbgeApiState());

            healthPlanNames.add(projection.getPlanName());
            locationDTOs.add(pharmacistLocationDTO);
            modalities.add(projection.getModality());
        });

        PharmacistProfileFlatProjection profile = flatResults
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado"));

        String fullName = profile.getFullName();
        String email = profile.getEmail();
        String crf = profile.getCrf();
        Boolean acceptsRemote = profile.getAcceptsRemote();

        PharmacistDTO pharmacistDTO = new PharmacistDTO();

        pharmacistDTO.setPharmacistLocations(locationDTOs);
        pharmacistDTO.setModalities(modalities);
        pharmacistDTO.setHealthPlanNames(healthPlanNames);

        pharmacistDTO.setFullName(fullName);
        pharmacistDTO.setEmail(email);
        pharmacistDTO.setCrf(crf);
        pharmacistDTO.setAcceptsRemote(acceptsRemote);

        return pharmacistDTO;
    }
}
