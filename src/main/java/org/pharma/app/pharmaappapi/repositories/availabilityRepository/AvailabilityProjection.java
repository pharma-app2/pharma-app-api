package org.pharma.app.pharmaappapi.repositories.availabilityRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AvailabilityProjection {
    UUID getId();
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
}
