CREATE TABLE IF NOT EXISTS patients_pharmacists (
    PRIMARY KEY (pharmacist_id, patient_id),

    pharmacist_id UUID NOT NULL,
    patient_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_pharmacist_assoc FOREIGN KEY (pharmacist_id) REFERENCES pharmacists(id) ON DELETE CASCADE,
    CONSTRAINT fk_patient_assoc FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Trigger para atualizar 'updated_at' na tabela de associação
CREATE OR REPLACE TRIGGER set_timestamp BEFORE UPDATE ON patients_pharmacists FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();