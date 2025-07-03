-- We will not create pharmacists now

  CREATE TABLE patients (
    id UUID PRIMARY KEY,
    cpf VARCHAR(20) NOT NULL,
    birthday DATE,
    user_id UUID NOT NULL UNIQUE,

    CONSTRAINT fk_patients_users
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT uk_patient_cpf UNIQUE (cpf)
);

