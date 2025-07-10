package org.pharma.app.pharmaappapi.repositories;

import org.pharma.app.pharmaappapi.security.models.users.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
