CREATE TABLE IF NOT EXISTS appointments_status (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(15) NOT NULL DEFAULT 'AGENDADO',

    CONSTRAINT uk_appointments_status_name UNIQUE (name)
);

INSERT INTO appointments_status(name) VALUES ('AGENDADO'), ('CONFIRMADO'), ('CONCLUÍDO'), ('CANCELADO'), ('NÃO COMPARECEU');

CREATE TABLE IF NOT EXISTS appointments_modality (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(15) NOT NULL DEFAULT 'PRESENCIAL',

    CONSTRAINT uk_appointments_modality_name UNIQUE (name)
);

INSERT INTO appointments_modality(name) VALUES ('PRESENCIAL'), ('TELECONSULTA');

CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    patient_id UUID NOT NULL,
    pharmacist_id UUID NOT NULL,

    scheduled_at TIMESTAMPTZ NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 30,

    appointments_status_id UUID NOT NULL,
    appointments_modality_id UUID NOT NULL,

    -- Anotações feitas pelo farmacêutico durante ou após a consulta.
    pharmacist_notes TEXT,
    -- Motivo da consulta, preenchido pelo paciente ao agendar.
    patient_reason TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- === Constraints de Chave Estrangeira ===
    CONSTRAINT fk_appointments_patients
        FOREIGN KEY (patient_id) REFERENCES patients(id)
        ON DELETE RESTRICT, -- Impede que um paciente com consultas seja deletado

    CONSTRAINT fk_appointments_pharmacists
        FOREIGN KEY (pharmacist_id) REFERENCES pharmacists(id)
        ON DELETE RESTRICT, -- Impede que um farmacêutico com consultas seja deletado

    CONSTRAINT fk_appointments_status
        FOREIGN KEY (appointments_status_id) REFERENCES appointments_status(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_appointments_modality
        FOREIGN KEY (appointments_modality_id) REFERENCES appointments_modality(id)
        ON DELETE RESTRICT
);

-- Trigger para atualizar o campo 'updated_at' automaticamente
CREATE OR REPLACE TRIGGER set_timestamp BEFORE UPDATE ON appointments FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();

-- Índices para otimizar buscas frequentes
CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointments_pharmacist_id ON appointments(pharmacist_id);
CREATE INDEX IF NOT EXISTS idx_appointments_scheduled_at ON appointments(scheduled_at);