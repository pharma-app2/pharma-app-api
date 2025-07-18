package org.pharma.app.pharmaappapi.services.appointmentService;

import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentPatientProjection;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentPharmacistProjection;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;

import java.util.Set;
import java.util.UUID;

public interface AppointmentService {
    CreateAppointmentDTO createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO);
    Set<AppointmentPatientProjection> getPatientFutureAppointments(UUID userId);
    Set<AppointmentPharmacistProjection> getPharmacistFutureAppointments(UUID userId);
    void deleteAppointment(UUID userId, UUID appointmentId);
}
