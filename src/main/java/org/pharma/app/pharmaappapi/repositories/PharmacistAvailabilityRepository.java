package org.pharma.app.pharmaappapi.repositories;

import org.pharma.app.pharmaappapi.models.appointments.PharmacistAvailability;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PharmacistAvailabilityRepository extends JpaRepository<PharmacistAvailability, UUID> {
    PharmacistAvailability findPharmacistAvailabilitiesByPharmacistAndAppointmentIsNull(Pharmacist pharmacist);
    Optional<PharmacistAvailability> findFirstById(UUID id);
}
