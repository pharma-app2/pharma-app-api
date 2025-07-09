package org.pharma.app.pharmaappapi.models.appointments;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "appointments_status",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_appointments_status_name",
                columnNames = "name"
        )
)
@Data
public class AppointmentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    private UUID id;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 15,
            message = "Appointment status name must have between 3 and 15 characters"
    )
    @Column(name = "name", nullable = false)
    private String name;
}
