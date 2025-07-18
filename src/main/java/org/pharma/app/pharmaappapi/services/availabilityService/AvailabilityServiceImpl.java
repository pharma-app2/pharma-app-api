package org.pharma.app.pharmaappapi.services.availabilityService;

import org.pharma.app.pharmaappapi.exceptions.ConflictException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentStatusName;
import org.pharma.app.pharmaappapi.models.availabilities.Availability;
import org.pharma.app.pharmaappapi.payloads.availabilityDTOs.*;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentRepository;
import org.pharma.app.pharmaappapi.repositories.availabilityRepository.OwnAvailabilityProjection;
import org.pharma.app.pharmaappapi.repositories.pharmacistRepository.ProfileRepository;
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
    private final ProfileRepository pharmacistRepository;
    private final AppointmentRepository appointmentRepository;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository, ProfileRepository pharmacistRepository, AppointmentRepository appointmentRepository) {
        this.availabilityRepository = availabilityRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Set<OwnAvailabilityDTO> getOwnAvailabilities(UUID userId, AvailabilityParameters params) {
        LocalDateTime startTime = params.getStartDate().atStartOfDay();
        LocalDateTime endTime = params.getEndDate().plusDays(1).atStartOfDay();

        Pharmacist pharmacist = pharmacistRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId.toString()));

        Set<OwnAvailabilityProjection> availabilitiesByStartAndEndDate = availabilityRepository
                .findOwnAvailabilitiesByStartAndEndDate(pharmacist.getId(), startTime, endTime);

        return availabilitiesByStartAndEndDate.stream().map(this::convertProjectionToDto).collect(Collectors.toSet());
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

    private OwnAvailabilityDTO convertProjectionToDto(OwnAvailabilityProjection projection) {
        if (projection == null) {
            return null;
        }

        // 1. Lógica para decidir qual ID usar
        UUID id = projection.getAppointmentId() != null ? projection.getAppointmentId() : projection.getAvailabilityId();

        // 2. Mapeamento direto
        LocalDateTime startTime = projection.getStartTime();
        Integer durationMinutes = projection.getDurationMinutes();

        // 3. Conversão de String para Enum
        AppointmentOrAvailability type = AppointmentOrAvailability.valueOf(projection.getType());

        // 4. Tratamento de campos que podem ser nulos na projeção
        // Para satisfazer o @NotNull do DTO, fornecemos valores padrão.
        String patientName = projection.getPatientName() != null ? projection.getPatientName() : "Vaga Disponível";

        AppointmentStatusName status = projection.getStatus() != null
            ? AppointmentStatusName.valueOf(projection.getStatus())
            : AppointmentStatusName.DISPONIVEL; // Supondo que você tenha um status 'DISPONIVEL' no enum

        // 5. Criação do DTO
        return new OwnAvailabilityDTO(id, startTime, durationMinutes, type, patientName, status);
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
