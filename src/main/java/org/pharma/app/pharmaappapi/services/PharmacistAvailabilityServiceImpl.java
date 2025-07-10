package org.pharma.app.pharmaappapi.services;

import org.pharma.app.pharmaappapi.exceptions.ConflictException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.pharmacistAvailabilities.PharmacistAvailability;
import org.pharma.app.pharmaappapi.payloads.pharmacistAvailabilityDTOs.PharmacistAvailabilityDTO;
import org.pharma.app.pharmaappapi.repositories.AppointmentRepository;
import org.pharma.app.pharmaappapi.repositories.PharmacistAvailabilityRepository;
import org.pharma.app.pharmaappapi.repositories.PharmacistRepository;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class PharmacistAvailabilityServiceImpl implements PharmacistAvailabilityService {
    private final PharmacistAvailabilityRepository pharmacistAvailabilityRepository;
    private final PharmacistRepository pharmacistRepository;
    private final AppointmentRepository appointmentRepository;

    public PharmacistAvailabilityServiceImpl(PharmacistAvailabilityRepository pharmacistAvailabilityRepository, PharmacistRepository pharmacistRepository, AppointmentRepository appointmentRepository) {
        this.pharmacistAvailabilityRepository = pharmacistAvailabilityRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void createPharmacistAvailability(UUID userId, PharmacistAvailabilityDTO pharmacistAvailabilityDTO) {
        Pharmacist pharmacist = pharmacistRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacist", "id", userId.toString()));

        OffsetDateTime scheduleStart = pharmacistAvailabilityDTO.getStartTime();
        OffsetDateTime scheduleEnd = scheduleStart.plusMinutes(pharmacistAvailabilityDTO.getDurationMinutes());

        boolean hasOverlapSchedule = pharmacist.getPharmacistAvailabilities().stream().anyMatch(
                avail -> {
                    OffsetDateTime start = avail.getStartTime();
                    Integer duration = avail.getDurationMinutes();
                    OffsetDateTime end = start.plusMinutes(duration);

                    return (scheduleEnd.isAfter(start) && scheduleStart.isBefore(end));
                }
        );

        if (hasOverlapSchedule) {
            throw new ConflictException("Schedule is already booked");
        }

        boolean hasAppointmentOverlap = appointmentRepository.hasOverlappingAppointment(pharmacist.getId(), scheduleStart, scheduleEnd);
        if (hasAppointmentOverlap) {
            throw new ConflictException("This time slot overlaps with an existing scheduled appointment.");
        }

        PharmacistAvailability pharmacistAvailability = new PharmacistAvailability();

        pharmacistAvailability.setDurationMinutes(pharmacistAvailabilityDTO.getDurationMinutes());
        pharmacistAvailability.setStartTime(scheduleStart);

        pharmacist.getPharmacistAvailabilities().add(pharmacistAvailability);
        pharmacistAvailability.setPharmacist(pharmacist);

        // It doesn't save a new pharmacist - if pharmacist has id, it updates. Otherwise, it creates a new one
        pharmacistRepository.save(pharmacist);
    }
}
