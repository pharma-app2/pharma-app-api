package org.pharma.app.pharmaappapi.repositories;

import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PharmacistRepository extends JpaRepository<Pharmacist, UUID> {
    Optional<Pharmacist> findFirstByUser_Id(UUID userId);
}
