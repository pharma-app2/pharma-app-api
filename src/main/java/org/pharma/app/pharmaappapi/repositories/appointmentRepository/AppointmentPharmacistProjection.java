package org.pharma.app.pharmaappapi.repositories.appointmentRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppointmentPharmacistProjection {
    UUID getId();
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
    String getPatientName();
    String getModality();
    String getStatus();
}
