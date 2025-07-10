package org.pharma.app.pharmaappapi.services;

import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;

public interface AppointmentService {
    CreateAppointmentDTO createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO);
}
