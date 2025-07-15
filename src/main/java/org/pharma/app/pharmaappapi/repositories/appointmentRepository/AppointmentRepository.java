package org.pharma.app.pharmaappapi.repositories.appointmentRepository;

import org.pharma.app.pharmaappapi.models.appointments.Appointment;
import org.pharma.app.pharmaappapi.security.models.users.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;
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
            @Param("startTime") LocalDateTime startTime
    );

    @Query(
            nativeQuery = true,
            value = "SELECT EXISTS (" +
                    "SELECT 1 FROM appointments a " +
                    "JOIN pharmacist_availabilities pa ON a.availability_id = pa.id " +
                    "WHERE pa.pharmacist_id = :pharmacistId " +
                    "AND :newAppointmentStart < (pa.start_time + (pa.duration_minutes * INTERVAL '1 minute')) " +
                    "AND :newAppointmentEnd > pa.start_time);"
    )
    boolean hasOverlappingAppointment(
            @Param("pharmacistId") UUID pharmacistId,
            @Param("newAppointmentStart") LocalDateTime newAppointmentStart,
            @Param("newAppointmentEnd") LocalDateTime newAppointmentEnd
    );

    @Query(
            nativeQuery = true,
            value = "SELECT a.id, u_pharmacist.full_name AS pharmacistName, pa.start_time AS startTime, " +
                    "pa.duration_minutes AS durationMinutes, aps.name AS status, am.name AS modality " +
                    "FROM appointments a " +
                    "LEFT JOIN appointments_status aps ON aps.id = a.appointments_status_id " +
                    "LEFT JOIN appointments_modality am ON am.id = a.appointments_modality_id " +
                    "LEFT JOIN patients pt ON pt.id = a.patient_id " +
                    "LEFT JOIN pharmacist_availabilities pa ON pa.id = a.availability_id " +
                    "LEFT JOIN pharmacists p ON p.id = pa.pharmacist_id " +
                    "LEFT JOIN users u_pharmacist ON u_pharmacist.id = p.user_id " +
                    "WHERE pt.user_id = :userIdPlaceholder " +
                    "AND aps.name IN ('AGENDADO', 'CONFIRMADO');"
    )
    Set<AppointmentProjection> findPatientFutureAppointments(
            @Param("userIdPlaceholder") UUID patientId
    );
}
