package org.pharma.app.pharmaappapi.repositories.patientRepository;

import java.util.UUID;

public interface PatientSearchByNameProjection {
    UUID getId();
    String getFullName();
    String getEmail();
}
