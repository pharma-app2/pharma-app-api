package org.pharma.app.pharmaappapi.models.appointments;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.pharma.app.pharmaappapi.models.availabilities.Availability;
import org.pharma.app.pharmaappapi.security.models.users.Patient;
import org.pharma.app.pharmaappapi.security.models.users.Pharmacist;

import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Size(
            max = 10_000,
            message = "Notes must have less than 10000 characters"
    )
    @Column(name = "pharmacist_notes", columnDefinition = "TEXT")
    @ToString.Include
    private String pharmacistNotes;

    @Size(
            max = 10_000,
            message = "Reason must have less than 10000 characters"
    )
    @Column(name = "patient_reason", columnDefinition = "TEXT")
    @ToString.Include
    private String patientReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointments_status_id", referencedColumnName = "id", nullable = false)
    private AppointmentStatus appointmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointments_modality_id", referencedColumnName = "id", nullable = false)
    private AppointmentModality appointmentModality;

    // TODO: test all possibilities for CRUD at appointments -> delete an user (pharmacist and/or patient) and see what happens with appointments. Try to handle it with
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacist_id", referencedColumnName = "id", nullable = false)
    private Pharmacist pharmacist;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(
        name = "availability_id", // Nome da coluna FK na tabela 'appointments'
        referencedColumnName = "id",
        unique = true // Garante que uma vaga só pode ser usada por uma consulta
    )
    private Availability availability;
}