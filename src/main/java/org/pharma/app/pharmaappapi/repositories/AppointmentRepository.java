package org.pharma.app.pharmaappapi.repositories;

import org.pharma.app.pharmaappapi.models.appointments.Appointment;
import org.pharma.app.pharmaappapi.security.models.users.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Boolean existsByPatient(Patient patient);

    @Query(
            nativeQuery = true,
            value = "SELECT EXISTS " +
                    "(SELECT 1 " +
                    "FROM appointments a " +
                    "JOIN appointments_status s ON a.appointments_status_id = s.id " +
                    "WHERE a.patient_id = ? AND s.name IN (?, ?) AND a.scheduled_at > ?);"
    )
    boolean patientAlreadyHasSchedule(
            @Param("patientIdPlaceholder") UUID patientIdPlaceholder,
            @Param("statusName1") String statusName1,
            @Param("statusName2") String statusName2);
}
