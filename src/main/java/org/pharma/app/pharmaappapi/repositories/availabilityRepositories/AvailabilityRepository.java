package org.pharma.app.pharmaappapi.repositories.availabilityRepositories;

import org.pharma.app.pharmaappapi.models.availabilities.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    Optional<Availability> findFirstById(UUID id);

    @Query(
            nativeQuery = true,
            value = "SELECT pa.start_time, pa.duration_minutes " +
                    "FROM pharmacist_availabilities pa " +
                    "WHERE pa.pharmacist_id = :pharmacistId " +
                    "AND pa.start_time >= :startTimePlaceholder " +
                    "AND pa.start_time < :endTimePlaceholder " +
                    "ORDER BY pa.start_time;"
    )
    Set<AvailabilityProjection> findAvailabilitiesByStartAndEndDate(
            @Param("pharmacistId") UUID pharmacistId,
            @Param("startTimePlaceholder") LocalDateTime startTime,
            @Param("endTimePlaceholder") LocalDateTime endTime);
}
