package org.pharma.app.pharmaappapi.services.appointmentService;

import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.ConflictException;
import org.pharma.app.pharmaappapi.exceptions.ForbiddenException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.appointments.*;
import org.pharma.app.pharmaappapi.models.availabilities.Availability;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.AppointmentDTO;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.AppointmentPatientDTO;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.EventType;
import org.pharma.app.pharmaappapi.repositories.AppointmentStatusRepository;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentPatientProjection;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentPharmacistProjection;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentRepository;
import org.pharma.app.pharmaappapi.repositories.availabilityRepository.AvailabilityRepository;
import org.pharma.app.pharmaappapi.repositories.patientRepository.PatientRepository;
import org.pharma.app.pharmaappapi.repositories.pharmacistRepository.ProfileRepository;
import org.pharma.app.pharmaappapi.security.models.users.Patient;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;
import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final static String DEFAULT_STATUS_NAME = AppointmentStatusName.AGENDADO.name();

    private final ModelMapper modelMapper;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;
    private final ProfileRepository pharmacistRepository;
    private final PatientRepository patientRepository;
    private final AvailabilityRepository pharmacistAvailabilityRepository;
    private final AvailabilityRepository availabilityRepository;

    public AppointmentServiceImpl(
            ModelMapper modelMapper,
            AppointmentRepository appointmentRepository,
            AppointmentStatusRepository appointmentStatusRepository,
            ProfileRepository pharmacistRepository,
            PatientRepository patientRepository,
            AvailabilityRepository pharmacistAvailabilityRepository, AvailabilityRepository availabilityRepository) {
        this.modelMapper = modelMapper;
        this.appointmentRepository = appointmentRepository;
        this.appointmentStatusRepository = appointmentStatusRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.patientRepository = patientRepository;
        this.pharmacistAvailabilityRepository = pharmacistAvailabilityRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    @Transactional
    public void deleteAppointment(UUID userId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId.toString()));

        // Verifique se o usuário que está pedindo a exclusão é o paciente ou o farmacêutico da consulta
        UUID patientUserId = appointment.getPatient().getUser().getId();
        UUID pharmacistUserId = appointment.getAvailability().getPharmacist().getUser().getId();
        if (!userId.equals(patientUserId) && !userId.equals(pharmacistUserId)) {
            throw new ForbiddenException("You are not authorized to delete this appointment.");
        }

        Availability availability = appointment.getAvailability();

        if (availability != null) {
            availability.setAppointment(null);
            // Não é estritamente necessário salvar aqui, o 'dirty checking' do @Transactional faria isso,
            // mas ser explícito ajuda na clareza.
            availabilityRepository.save(availability);
        }

        appointmentRepository.delete(appointment);
    }

    @Override
    @Transactional
    public CreateAppointmentDTO createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO) {
        UUID userId = userDetails.getId();
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority();

        UUID patientId = createAppointmentDTO.getPatientId();
        UUID pharmacistAvailabilityId = createAppointmentDTO.getAvailabilityId();
        Boolean isRemote = createAppointmentDTO.getIsRemote();

        Availability availability = pharmacistAvailabilityRepository.findFirstById(pharmacistAvailabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade", "id", pharmacistAvailabilityId.toString()));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", patientId.toString()));

        Pharmacist pharmacist = pharmacistRepository
                .findById(availability.getPharmacist().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Farmacêutico", "id", availability.getPharmacist().getId().toString()));

        if (userRole.equals(RoleName.ROLE_PATIENT.name()) && !patient.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Pacientes só podem criar consultas para si mesmos");
        }

        if (userRole.equals(RoleName.ROLE_PHARMACIST.name()) && !pharmacist.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Farmacêuticos só podem criar consultas para si mesmos");
        }

        AppointmentStatus appointmentStatus = appointmentStatusRepository.findFirstByName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "nome", DEFAULT_STATUS_NAME));

        boolean patientHasAppointment = appointmentRepository.patientAlreadyHasSchedule(patient.getId(),AppointmentStatusName.AGENDADO.name(), AppointmentStatusName.CONFIRMADO.name(), availability.getStartTime());
        if (patientHasAppointment) {
            throw new ConflictException("Essa paciente já possui consulta agendada");
        }

        // Verifica se a vaga escolhida já está associada a outra consulta.
        if (availability.getAppointment() != null) {
            throw new ConflictException("Esse horário já está agendado");
        }

        Appointment appointment = new Appointment();

        appointment.setAppointmentStatus(appointmentStatus);
        appointment.setPatient(patient);
        appointment.setIsRemote(isRemote);

        appointment.setAvailability(availability);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setId(savedAppointment.getId().toString());
        appointmentDTO.setStartTime(savedAppointment.getAvailability().getStartTime());
        appointmentDTO.setDurationMinutes(savedAppointment.getAvailability().getDurationMinutes());
        appointmentDTO.setType(EventType.APPOINTMENT);

        AppointmentPatientDTO appointmentPatient = new AppointmentPatientDTO();
        appointmentPatient.setName(patient.getUser().getFullName());
        appointmentPatient.setEmail(patient.getUser().getEmail());
        appointmentPatient.setId(patientId);
        appointmentDTO.setPatient(appointmentPatient);

        return modelMapper.map(savedAppointment, CreateAppointmentDTO.class);
    }

    @Override
    public Set<AppointmentPatientProjection> getPatientFutureAppointments(UUID userId) {
        return appointmentRepository.findPatientFutureAppointments(userId);
    }

    @Override
    public Set<AppointmentDTO> getPharmacistFutureAppointments(UUID userId) {
        Set<AppointmentPharmacistProjection> projections = appointmentRepository.findPharmacistFutureAppointments(userId);

        return projections.stream().map(projection -> {
            AppointmentDTO appointmentDTO = new AppointmentDTO();

            appointmentDTO.setId(projection.getId().toString());
            appointmentDTO.setStartTime(projection.getStartTime());
            appointmentDTO.setDurationMinutes(projection.getDurationMinutes());
            appointmentDTO.setStatus(AppointmentStatusName.valueOf(projection.getStatus()));
            appointmentDTO.setType(EventType.APPOINTMENT);

            AppointmentPatientDTO appointmentPatientDTO = new AppointmentPatientDTO();
            appointmentPatientDTO.setId(projection.getPatientId());
            appointmentPatientDTO.setName(projection.getPatientName());
            appointmentPatientDTO.setEmail(projection.getPatientEmail());

            appointmentDTO.setPatient(appointmentPatientDTO);

            return appointmentDTO;
        }).collect(Collectors.toSet());
    }
}
