package org.pharma.app.pharmaappapi.services;

import org.pharma.app.pharmaappapi.models.appointments.AppointmentModality;
import org.pharma.app.pharmaappapi.payloads.appointmentModalityDTOs.AppointmentModalityDTO;
import org.pharma.app.pharmaappapi.repositories.appointmentModalityRepository.AppointmentModalityRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class AppointmentModalityServiceImpl implements AppointmentModalityService {
    private final AppointmentModalityRepository appointmentModalityRepository;

    public AppointmentModalityServiceImpl(AppointmentModalityRepository appointmentModalityRepository) {
        this.appointmentModalityRepository = appointmentModalityRepository;
    }

    @Override
    public Set<AppointmentModalityDTO> getAppointmentModalitiesFromPharmacist(UUID userId) {
        return appointmentModalityRepository.findByPharmacistId(userId);
    }
}
