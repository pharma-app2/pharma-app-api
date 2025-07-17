package org.pharma.app.pharmaappapi.repositories.appointmentModalityRepository;

import org.pharma.app.pharmaappapi.models.appointments.AppointmentModality;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentModalityName;
import org.pharma.app.pharmaappapi.payloads.appointmentModalityDTOs.AppointmentModalityDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface AppointmentModalityRepository extends JpaRepository<AppointmentModality, UUID> {
    Optional<AppointmentModality> findFirstById(UUID id);

    @Query(
            nativeQuery = true,
            value = "SELECT am.id, am.name " +
                    "FROM pharmacists_appointments_modality pam " +
                    "JOIN appointments_modality am " +
                    "ON pam.appointments_modality_id = am.id " +
                    "WHERE pam.pharmacist_id = :pharmacistId;"
    )
    Set<AppointmentModalityDTO> findByPharmacistId(@Param("pharmacistId") UUID pharmacistId);

    Set<AppointmentModality> findAllByNameIn(Set<AppointmentModalityName> names);
}
