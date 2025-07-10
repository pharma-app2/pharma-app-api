package org.pharma.app.pharmaappapi.repositories;

import org.pharma.app.pharmaappapi.models.appointments.Appointment;
import org.pharma.app.pharmaappapi.security.models.users.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Boolean existsByPatient(Patient patient);

    @Query(
        nativeQuery = true,
        value = "SELECT EXISTS (" +
                "SELECT 1 " +
                "FROM appointments a " +
                "JOIN appointments_status s ON a.appointments_status_id = s.id " +
                "JOIN pharmacist_availabilities pa ON a.availability_id = pa.id " +
                "WHERE a.patient_id = :patientId " +
                "AND s.name IN (:statusName1, :statusName2) " +
                "AND pa.start_time > :startTime);"
    )
    boolean patientAlreadyHasSchedule(
            @Param("patientId") UUID patientId,
            @Param("statusName1") String statusName1,
            @Param("statusName2") String statusName2,
            @Param("startTime") OffsetDateTime startTime
    );

    @Query(
        nativeQuery = true,
        value = "SELECT EXISTS (" +
                "SELECT 1 FROM appointments a " +
                "JOIN pharmacist_availabilities pa ON a.availability_id = pa.id " +
                "WHERE a.pharmacist_id = :pharmacistId " +
                "AND :newAppointmentStart < (pa.start_time + (pa.duration_minutes * INTERVAL '1 minute')) " +
                "AND :newAppointmentEnd > pa.start_time);"
    )
    boolean hasOverlappingAppointment(
            @Param("pharmacistId") UUID pharmacistId,
            @Param("newAppointmentStart") OffsetDateTime newAppointmentStart,
            @Param("newAppointmentEnd") OffsetDateTime newAppointmentEnd
    );
}
