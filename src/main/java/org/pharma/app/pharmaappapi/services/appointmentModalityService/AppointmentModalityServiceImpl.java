package org.pharma.app.pharmaappapi.services.appointmentModalityService;

import org.pharma.app.pharmaappapi.exceptions.ConflictException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentModality;
import org.pharma.app.pharmaappapi.payloads.appointmentModalityDTOs.AppointmentModalityDTO;
import org.pharma.app.pharmaappapi.repositories.PharmacistRepository;
import org.pharma.app.pharmaappapi.repositories.appointmentModalityRepository.AppointmentModalityRepository;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class AppointmentModalityServiceImpl implements AppointmentModalityService {
    private final AppointmentModalityRepository appointmentModalityRepository;
    private final PharmacistRepository pharmacistRepository;

    public AppointmentModalityServiceImpl(AppointmentModalityRepository appointmentModalityRepository, PharmacistRepository pharmacistRepository) {
        this.appointmentModalityRepository = appointmentModalityRepository;
        this.pharmacistRepository = pharmacistRepository;
    }

    @Override
    public void createAppointmentModalityForPharmacist(UUID userId, UUID modalityId) {
        Pharmacist pharmacist = pharmacistRepository.findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        AppointmentModality modality = appointmentModalityRepository
                .findFirstById(modalityId).orElseThrow(() -> new ResourceNotFoundException("Modality", "id", modalityId.toString()));

        if (pharmacist.getAvailableModalities().contains(modality)) {
            throw new ConflictException("Essa modalidade de consulta já foi registrada");
        }

        pharmacist.getAvailableModalities().add(modality);

        pharmacistRepository.save(pharmacist);
    }

    @Override
    public Set<AppointmentModalityDTO> getAppointmentModalitiesFromPharmacist(UUID userId) {
        return appointmentModalityRepository.findByPharmacistId(userId);
    }
}
