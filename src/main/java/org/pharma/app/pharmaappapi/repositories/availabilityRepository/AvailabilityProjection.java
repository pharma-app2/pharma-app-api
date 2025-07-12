package org.pharma.app.pharmaappapi.repositories.availabilityRepository;

import java.time.LocalDateTime;

public interface AvailabilityProjection {
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
}
