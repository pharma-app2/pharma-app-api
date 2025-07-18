package org.pharma.app.pharmaappapi.repositories.availabilityRepository;

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
            value = "SELECT pa.id, pa.start_time, pa.duration_minutes " +
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

     @Query(
        nativeQuery = true,
        value = "SELECT " +
                "    avail.id AS availabilityId, " +
                "    avail.start_time AS startTime, " +
                "    avail.duration_minutes AS durationMinutes, " +
                "    app.id AS appointmentId, " +
                "    u.full_name AS patientName, " +
                "    s.name AS status, " +
                "    CASE WHEN app.id IS NOT NULL THEN 'APPOINTMENT' ELSE 'AVAILABILITY' END AS type " +
                "FROM " +
                "    pharmacist_availabilities avail " +
                "LEFT JOIN " +
                "    appointments app ON avail.id = app.availability_id " +
                "LEFT JOIN " +
                "    patients p ON app.patient_id = p.id " +
                "LEFT JOIN " +
                "    users u ON p.user_id = u.id " +
                "LEFT JOIN " +
                "    appointments_status s ON app.appointments_status_id = s.id " +
                "WHERE " +
                "    avail.pharmacist_id = :pharmacistId " +
                "    AND avail.start_time >= :startDateTime " +
                "    AND avail.start_time < :endDateTime " +
                "ORDER BY " +
                "    avail.start_time ASC"
    )
    Set<OwnAvailabilityProjection> findOwnAvailabilitiesByStartAndEndDate(
            @Param("pharmacistId") UUID pharmacistId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
