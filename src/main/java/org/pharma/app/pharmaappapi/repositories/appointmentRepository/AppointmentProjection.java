package org.pharma.app.pharmaappapi.repositories.appointmentRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppointmentProjection {
    UUID getId();
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
    String getPharmacistName();
    String getModality();
    String getStatus();
}
