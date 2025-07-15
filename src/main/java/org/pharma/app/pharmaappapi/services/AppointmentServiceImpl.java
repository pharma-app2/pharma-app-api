package org.pharma.app.pharmaappapi.services;

import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.ConflictException;
import org.pharma.app.pharmaappapi.exceptions.ForbiddenException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.appointments.Appointment;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentModality;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentStatus;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentStatusName;
import org.pharma.app.pharmaappapi.models.availabilities.Availability;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.AppointmentDTO;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.pharma.app.pharmaappapi.repositories.AppointmentRepository;
import org.pharma.app.pharmaappapi.repositories.AppointmentStatusRepository;
import org.pharma.app.pharmaappapi.repositories.PatientRepository;
import org.pharma.app.pharmaappapi.repositories.PharmacistRepository;
import org.pharma.app.pharmaappapi.repositories.appointmentModalityRepository.AppointmentModalityRepository;
import org.pharma.app.pharmaappapi.repositories.availabilityRepository.AvailabilityRepository;
import org.pharma.app.pharmaappapi.security.models.users.Patient;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final static String DEFAULT_STATUS_NAME = AppointmentStatusName.AGENDADO.name();

    private final ModelMapper modelMapper;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentModalityRepository appointmentModalityRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;
    private final PharmacistRepository pharmacistRepository;
    private final PatientRepository patientRepository;
    private final AvailabilityRepository pharmacistAvailabilityRepository;

    public AppointmentServiceImpl(
            ModelMapper modelMapper,
            AppointmentRepository appointmentRepository,
            AppointmentModalityRepository appointmentModalityRepository,
            AppointmentStatusRepository appointmentStatusRepository,
            PharmacistRepository pharmacistRepository,
            PatientRepository patientRepository,
            AvailabilityRepository pharmacistAvailabilityRepository) {
        this.modelMapper = modelMapper;
        this.appointmentRepository = appointmentRepository;
        this.appointmentModalityRepository = appointmentModalityRepository;
        this.appointmentStatusRepository = appointmentStatusRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.patientRepository = patientRepository;
        this.pharmacistAvailabilityRepository = pharmacistAvailabilityRepository;
    }

    @Override
    @Transactional
    public CreateAppointmentDTO createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO) {
        UUID userId = userDetails.getId();
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority();

        UUID patientId = createAppointmentDTO.getPatientId();
        UUID pharmacistId = createAppointmentDTO.getPharmacistId();
        UUID modalityId = createAppointmentDTO.getModalityId();
        UUID pharmacistAvailabilityId = createAppointmentDTO.getAvailabilityId();

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

        Availability availability = pharmacistAvailabilityRepository.findFirstById(pharmacistAvailabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability", "id", pharmacistAvailabilityId.toString()));


        boolean isModalityAvailable = pharmacist.getAvailableModalities().contains(appointmentModality);
        if (!isModalityAvailable) {
            throw new ConflictException("This appointment modality is not available for this pharmacist");
        }

        boolean patientHasAppointment = appointmentRepository.patientAlreadyHasSchedule(patient.getId(),AppointmentStatusName.AGENDADO.name(), AppointmentStatusName.CONFIRMADO.name(), availability.getStartTime());
        if (patientHasAppointment) {
            throw new ConflictException("Patient already have an appointment");
        }

        // Verifica se a vaga escolhida já está associada a outra consulta.
        if (availability.getAppointment() != null) {
            throw new ConflictException("This availability slot is already booked.");
        }

        Appointment appointment = new Appointment();

        appointment.setAppointmentModality(appointmentModality);
        appointment.setAppointmentStatus(appointmentStatus);
        appointment.setPatient(patient);
        appointment.setPharmacist(pharmacist);

        appointment.setAvailability(availability);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return modelMapper.map(savedAppointment, CreateAppointmentDTO.class);
    }

    @Override
    public Set<AppointmentDTO> getPatientFutureAppointments(UUID userId) {
        Patient patient = patientRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId.toString()));

        Set<Appointment> appointments = patient.getAppointments().stream().filter(
                appointment -> {
                    String booked = AppointmentStatusName.AGENDADO.name();
                    String confirmed = AppointmentStatusName.CONFIRMADO.name();

                    Boolean isBooked = appointment.getAppointmentStatus().getName().equals(booked);
                    Boolean isConfirmed = appointment.getAppointmentStatus().getName().equals(confirmed);

                    return isBooked || isConfirmed;
                }
        ).collect(Collectors.toSet());

        return appointments
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                .collect(Collectors.toSet());
    }
}
