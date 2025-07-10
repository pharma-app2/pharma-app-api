package org.pharma.app.pharmaappapi.services;

import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.ConflictException;
import org.pharma.app.pharmaappapi.exceptions.ForbiddenException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.appointments.*;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.pharma.app.pharmaappapi.repositories.*;
import org.pharma.app.pharmaappapi.security.models.users.Patient;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.pharma.app.pharmaappapi.security.repositories.AuthRepository;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final static Integer DEFAULT_DURATION_MINUTES = 30;
    private final static String DEFAULT_STATUS_NAME = AppointmentStatusName.AGENDADO.name();

    private final ModelMapper modelMapper;
    private final AppointmentRepository appointmentRepository;
    private final AuthRepository authRepository;
    private final AppointmentModalityRepository appointmentModalityRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;
    private final PharmacistRepository pharmacistRepository;
    private final PatientRepository patientRepository;
    private final PharmacistAvailabilityRepository pharmacistAvailabilityRepository;

    public AppointmentServiceImpl(
            ModelMapper modelMapper,
            AuthRepository authRepository,
            AppointmentRepository appointmentRepository,
            AppointmentModalityRepository appointmentModalityRepository,
            AppointmentStatusRepository appointmentStatusRepository,
            PharmacistRepository pharmacistRepository,
            PatientRepository patientRepository,
            PharmacistAvailabilityRepository pharmacistAvailabilityRepository
    ) {
        this.modelMapper = modelMapper;
        this.authRepository = authRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentModalityRepository = appointmentModalityRepository;
        this.appointmentStatusRepository = appointmentStatusRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.patientRepository = patientRepository;
        this.pharmacistAvailabilityRepository = pharmacistAvailabilityRepository;
    }

    @Override
    public CreateAppointmentDTO createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO) {
        UUID userId = userDetails.getId();
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority();

        UUID patientId = createAppointmentDTO.getPatientId();
        UUID pharmacistId = createAppointmentDTO.getPharmacistId();
        UUID modalityId = createAppointmentDTO.getModalityId();
        UUID pharmacistAvailabilityId = createAppointmentDTO.getPharmacistAvailabilityId();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId.toString()));

        Pharmacist pharmacist = pharmacistRepository.findById(pharmacistId)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacist", "id", pharmacistId.toString()));

        if (userRole.equals(RoleName.ROLE_PATIENT.name()) && !patient.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Patients can only create appointments for himself");
        }

        if (userRole.equals(RoleName.ROLE_PHARMACIST.name()) && !pharmacist.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Pharmacist can only create appointments for himself");
        }

        AppointmentModality appointmentModality = appointmentModalityRepository.findFirstById(modalityId)
                .orElseThrow(() -> new ResourceNotFoundException("Modality", "id", modalityId.toString()));

        AppointmentStatus appointmentStatus = appointmentStatusRepository.findFirstByName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "name", DEFAULT_STATUS_NAME));

        PharmacistAvailability availability = pharmacistAvailabilityRepository.findFirstById(pharmacistAvailabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability", "id", pharmacistAvailabilityId.toString()));


        boolean isModalityAvailable = pharmacist.getAvailableModalities().contains(appointmentModality);
        if (!isModalityAvailable) {
            throw new ConflictException("This appointment modality is not available for this pharmacist");
        }

        boolean patientHasAppointment = appointmentRepository.patientAlreadyHasSchedule(patient.getId(),AppointmentStatusName.AGENDADO.name(), AppointmentStatusName.CONFIRMADO.name());
        if (patientHasAppointment) {
            throw new ConflictException("Patient already have an appointment");
        }

        // TODO: usar no método que cria horarios disponiveis pelo farmaceutico
//        OffsetDateTime newAppointmentStart = availability.getStartTime();
//        OffsetDateTime newAppointmentEnd = newAppointmentStart.plusMinutes(availability.getDurationMinutes());
//
//        boolean hasOverlapSchedule = pharmacist.getPharmacistAvailability().stream().anyMatch(
//                avail -> {
//                    OffsetDateTime start = avail.getStartTime();
//                    Integer duration = avail.getDurationMinutes();
//                    OffsetDateTime end = start.plusMinutes(duration);
//
//                    return (newAppointmentEnd.isAfter(start) && newAppointmentStart.isBefore(end));
//                }
//        );

        // Verifica se a vaga escolhida já está associada a outra consulta.
        if (availability.getAppointment() != null) {
            throw new ConflictException("This availability slot is already booked.");
        }

        Appointment appointment = new Appointment();

        appointment.setAppointmentModality(appointmentModality);

        appointment.setAppointmentStatus(appointmentStatus);

        appointment.setPatient(patient);
//        patient.getAppointments().add(appointment);

        appointment.setPharmacist(pharmacist);
//        pharmacist.getAppointments().add(appointment);

//        appointment.setPharmacistAvailability(availability);
        availability.setAppointment(appointment);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return modelMapper.map(savedAppointment, CreateAppointmentDTO.class);
    }
}
