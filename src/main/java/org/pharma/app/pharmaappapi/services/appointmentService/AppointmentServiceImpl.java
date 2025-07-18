package org.pharma.app.pharmaappapi.services.appointmentService;

import org.modelmapper.ModelMapper;
import org.pharma.app.pharmaappapi.exceptions.ConflictException;
import org.pharma.app.pharmaappapi.exceptions.ForbiddenException;
import org.pharma.app.pharmaappapi.exceptions.ResourceNotFoundException;
import org.pharma.app.pharmaappapi.models.appointments.Appointment;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentModality;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentStatus;
import org.pharma.app.pharmaappapi.models.appointments.AppointmentStatusName;
import org.pharma.app.pharmaappapi.models.availabilities.Availability;
import org.pharma.app.pharmaappapi.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentPatientProjection;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentPharmacistProjection;
import org.pharma.app.pharmaappapi.repositories.appointmentRepository.AppointmentRepository;
import org.pharma.app.pharmaappapi.repositories.AppointmentStatusRepository;
import org.pharma.app.pharmaappapi.repositories.PatientRepository;
import org.pharma.app.pharmaappapi.repositories.pharmacistRepository.ProfileRepository;
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

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final static String DEFAULT_STATUS_NAME = AppointmentStatusName.AGENDADO.name();

    private final ModelMapper modelMapper;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentModalityRepository appointmentModalityRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;
    private final ProfileRepository pharmacistRepository;
    private final PatientRepository patientRepository;
    private final AvailabilityRepository pharmacistAvailabilityRepository;

    public AppointmentServiceImpl(
            ModelMapper modelMapper,
            AppointmentRepository appointmentRepository,
            AppointmentModalityRepository appointmentModalityRepository,
            AppointmentStatusRepository appointmentStatusRepository,
            ProfileRepository pharmacistRepository,
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
        UUID modalityId = createAppointmentDTO.getModalityId();
        UUID pharmacistAvailabilityId = createAppointmentDTO.getAvailabilityId();

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

        AppointmentModality appointmentModality = appointmentModalityRepository.findFirstById(modalityId)
                .orElseThrow(() -> new ResourceNotFoundException("Modalidade", "id", modalityId.toString()));

        AppointmentStatus appointmentStatus = appointmentStatusRepository.findFirstByName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "nome", DEFAULT_STATUS_NAME));

        boolean isModalityAvailable = pharmacist.getAvailableModalities().contains(appointmentModality);
        if (!isModalityAvailable) {
            throw new ConflictException("Modalidade de consulta não disponível para esse farmacêutico");
        }

        boolean patientHasAppointment = appointmentRepository.patientAlreadyHasSchedule(patient.getId(),AppointmentStatusName.AGENDADO.name(), AppointmentStatusName.CONFIRMADO.name(), availability.getStartTime());
        if (patientHasAppointment) {
            throw new ConflictException("Essa paciente já possui consulta agendada");
        }

        // Verifica se a vaga escolhida já está associada a outra consulta.
        if (availability.getAppointment() != null) {
            throw new ConflictException("Esse horário já está agendado");
        }

        Appointment appointment = new Appointment();

        appointment.setAppointmentModality(appointmentModality);
        appointment.setAppointmentStatus(appointmentStatus);
        appointment.setPatient(patient);

        appointment.setAvailability(availability);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return modelMapper.map(savedAppointment, CreateAppointmentDTO.class);
    }

    @Override
    public Set<AppointmentPatientProjection> getPatientFutureAppointments(UUID userId) {
        return appointmentRepository.findPatientFutureAppointments(userId);
    }

    @Override
    public Set<AppointmentPharmacistProjection> getPharmacistFutureAppointments(UUID userId) {
        return appointmentRepository.findPharmacistFutureAppointments(userId);
    }
}
