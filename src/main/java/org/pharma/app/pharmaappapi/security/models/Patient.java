package org.pharma.app.pharmaappapi.security.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
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

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 10,
            message = "Field birthday must have between 3 and 10 characters"
    )
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday = LocalDate.now();
}
