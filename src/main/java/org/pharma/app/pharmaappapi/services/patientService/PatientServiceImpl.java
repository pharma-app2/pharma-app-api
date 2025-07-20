package org.pharma.app.pharmaappapi.services.patientService;

import org.pharma.app.pharmaappapi.payloads.patientDTOs.PatientSearchByNameDTO;
import org.pharma.app.pharmaappapi.repositories.patientRepository.PatientRepository;
import org.pharma.app.pharmaappapi.repositories.patientRepository.PatientSearchByNameProjection;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Set<PatientSearchByNameDTO> getProfilesByName(String name) {
        if (name.isBlank()) return new HashSet<>();

        Set<PatientSearchByNameProjection> patients = patientRepository.findByName(name);

        return patients.stream().map(projection -> {
            PatientSearchByNameDTO patientDTO = new PatientSearchByNameDTO();

            patientDTO.setId(projection.getId());
            patientDTO.setFullName(projection.getFullName());
            patientDTO.setEmail(projection.getEmail());

            return patientDTO;
        }).collect(Collectors.toSet());
    }
}
