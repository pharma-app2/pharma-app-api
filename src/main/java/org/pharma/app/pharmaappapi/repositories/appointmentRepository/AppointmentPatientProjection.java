package org.pharma.app.pharmaappapi.repositories.appointmentRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppointmentPatientProjection {
    UUID getId();
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
    String getPatientName();
    String getStatus();
}
