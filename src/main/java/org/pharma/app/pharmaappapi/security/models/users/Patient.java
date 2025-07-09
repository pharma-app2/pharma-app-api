package org.pharma.app.pharmaappapi.security.models.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.pharma.app.pharmaappapi.models.appointments.Appointment;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "patients",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "cpf",
                        name = "uk_patient_cpf"
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Patient {
    public Patient(String cpf) {
        this.cpf = cpf;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 20,
            message = "Field cpf must have between 3 and 20 characters"
    )
    @Column(name = "cpf", nullable = false)
    private String cpf;

    @Past(message = "Field birthday must be a past date")
    @Column(name = "birthday")
    private LocalDate birthday;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    @ToString.Exclude // exclude lazy initializations (because when toString() calls getPatient() at an User instance, the JPA session is already closed due to lazy initialization. It leads to a LazyInitializationException)
    private User user;

    @ManyToMany(mappedBy = "patients", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Pharmacist> pharmacists = new HashSet<>();

    // PERSIST - when we save a new patient with new appointments, these appointments will be saved with the patient
    // MERGE - when we update a pre-existing patient and add an appointment to it, this appointment will be saved with the patient.
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<Appointment> appointments = new HashSet<>();
}
