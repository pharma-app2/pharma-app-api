package org.pharma.app.pharmaappapi.services.availabilityService;

import org.pharma.app.pharmaappapi.exceptions.ConflictException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.availabilities.Availability;
import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.AvailabilityCreateDTO;
import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.AvailabilityParameters;
import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.CustomLocalDateTime;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentRepository;
import org.pharma.app.pharmaappapi.repositories.pharmacistRepository.PharmacistRepository;
import org.pharma.app.pharmaappapi.repositories.availabilityRepository.AvailabilityProjection;
import org.pharma.app.pharmaappapi.repositories.availabilityRepository.AvailabilityRepository;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final PharmacistRepository pharmacistRepository;
    private final AppointmentRepository appointmentRepository;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository, PharmacistRepository pharmacistRepository, AppointmentRepository appointmentRepository) {
        this.availabilityRepository = availabilityRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Set<AvailabilityCreateDTO> getAvailabilitiesFromPharmacist(UUID pharmacistId, AvailabilityParameters params) {
        LocalDateTime startTime = params.getStartDate().atStartOfDay();
        LocalDateTime endTime = params.getEndDate().plusDays(1).atStartOfDay();

        pharmacistRepository
                .findById(pharmacistId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmacêutico", "id", pharmacistId.toString()));

        Set<AvailabilityProjection> availabilitiesByStartAndEndDate = availabilityRepository
                .findAvailabilitiesByStartAndEndDate(pharmacistId, startTime, endTime);

        return availabilitiesByStartAndEndDate.stream().map(
                avail -> {
                    LocalDateTime availabilityStartTime = avail.getStartTime();
                    Integer year = availabilityStartTime.getYear();
                    Integer month = availabilityStartTime.getMonthValue();
                    Integer day = availabilityStartTime.getDayOfMonth();
                    Integer hour = availabilityStartTime.getHour();
                    Integer minute = availabilityStartTime.getMinute();

                    CustomLocalDateTime customLocalDateTime = new CustomLocalDateTime(year, month, day, hour, minute);
                    Integer durationMinutes = avail.getDurationMinutes();

                    UUID id = avail.getId();

                    return new AvailabilityCreateDTO(id, customLocalDateTime, durationMinutes);
                }
        ).collect(Collectors.toSet());
    }

    @Override
    public void createAvailability(UUID userId, AvailabilityCreateDTO availabilityDTO) {
        Pharmacist pharmacist = pharmacistRepository.findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmacêutico", "id", userId.toString()));

        CustomLocalDateTime startLocalDateTime = availabilityDTO.getStartLocalDateTime();

        LocalDateTime scheduleStart = LocalDateTime.of(
                startLocalDateTime.getYear(),
                startLocalDateTime.getMonth(),
                startLocalDateTime.getDay(),
                startLocalDateTime.getHour(),
                startLocalDateTime.getMinute());
        LocalDateTime scheduleEnd = scheduleStart.plusMinutes(availabilityDTO.getDurationMinutes());

        if (verifyPharmacistHasOverlapSchedule(pharmacist, scheduleStart, scheduleEnd)) {
            throw new ConflictException("Schedule is already booked");
        }

        if (appointmentRepository.hasOverlappingAppointment(pharmacist.getId(), scheduleStart, scheduleEnd)) {
            throw new ConflictException("This time slot overlaps with an existing scheduled appointment.");
        }

        if (scheduleStart.isBefore(LocalDateTime.now())) {
            throw new ConflictException("Schedule can't be on past");
        }

        Availability availability = new Availability();

        availability.setDurationMinutes(availabilityDTO.getDurationMinutes());
        availability.setStartTime(scheduleStart);

        pharmacist.getAvailabilities().add(availability);
        availability.setPharmacist(pharmacist);

        // It doesn't save a new pharmacist - if pharmacist has id, it updates. Otherwise, it creates a new one
        pharmacistRepository.save(pharmacist);
    }

    private boolean verifyPharmacistHasOverlapSchedule(Pharmacist pharmacist,
                                                       LocalDateTime scheduleStart,
                                                       LocalDateTime scheduleEnd) {
        return pharmacist.getAvailabilities().stream().anyMatch(
                avail -> {
                    LocalDateTime start = avail.getStartTime();
                    Integer duration = avail.getDurationMinutes();
                    LocalDateTime end = start.plusMinutes(duration);

                    return (scheduleEnd.isAfter(start) && scheduleStart.isBefore(end));
                }
        );
    }
}
