package org.pharma.app.pharmaappapi.repositories;

import org.pharma.app.pharmaappapi.models.appointments.AppointmentModality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentModalityRepository extends JpaRepository<AppointmentModality, UUID> {
    Optional<AppointmentModality> findFirstById(UUID id);
}
