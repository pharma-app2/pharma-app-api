package org.pharma.app.pharmaappapi.services.patientService;

import org.pharma.app.pharmaappapi.payloads.patientDTOs.PatientSearchByNameDTO;

import java.util.Set;

public interface PatientService {
    Set<PatientSearchByNameDTO> getProfilesByName(String name);
}
