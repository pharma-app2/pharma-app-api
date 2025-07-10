package org.pharma.app.pharmaappapi.repositories;

import org.pharma.app.pharmaappapi.models.appointments.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatus, UUID> {
    Optional<AppointmentStatus> findFirstByName(String statusName);
}
