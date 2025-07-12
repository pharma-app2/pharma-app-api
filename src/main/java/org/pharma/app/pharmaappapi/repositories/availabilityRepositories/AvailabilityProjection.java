package org.pharma.app.pharmaappapi.repositories.availabilityRepositories;

import java.time.LocalDateTime;

public interface AvailabilityProjection {
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
}
