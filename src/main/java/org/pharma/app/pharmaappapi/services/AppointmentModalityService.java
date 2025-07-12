package org.pharma.app.pharmaappapi.services;

import org.pharma.app.pharmaappapi.payloads.appointmentModalityDTOs.AppointmentModalityDTO;

import java.util.Set;
import java.util.UUID;

public interface AppointmentModalityService {
    Set<AppointmentModalityDTO> getAppointmentModalitiesFromPharmacist(UUID pharmacistId);
    void createAppointmentModalityForPharmacist(UUID userId, UUID appointmentModalityId);
}
