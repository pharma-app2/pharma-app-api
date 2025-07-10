package org.pharma.app.pharmaappapi.repositories;

import org.pharma.app.pharmaappapi.models.appointments.Appointment;
import org.pharma.app.pharmaappapi.security.models.users.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Boolean existsByPatient(Patient patient);
}
