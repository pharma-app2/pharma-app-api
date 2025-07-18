package org.pharma.app.pharmaappapi.repositories.availabilityRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OwnAvailabilityProjection {
    UUID getAvailabilityId();
    
    UUID getAppointmentId();

    LocalDateTime getStartTime();

    Integer getDurationMinutes();
    
    String getType();

    String getPatientName();

    String getStatus();
}