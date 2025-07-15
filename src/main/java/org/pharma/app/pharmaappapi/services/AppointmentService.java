package org.pharma.app.pharmaappapi.services;

import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.AppointmentDTO;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;

import java.util.Set;
import java.util.UUID;

public interface AppointmentService {
    CreateAppointmentDTO createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO);
    Set<AppointmentDTO> getPatientFutureAppointments(UUID userId);
}
