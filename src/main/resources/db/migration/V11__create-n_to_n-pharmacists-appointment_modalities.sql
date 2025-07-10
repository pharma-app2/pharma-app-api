CREATE TABLE IF NOT EXISTS pharmacists_appointments_modality (
    PRIMARY KEY (pharmacist_id, appointments_modality_id),

    pharmacist_id UUID NOT NULL,
    appointments_modality_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_assoc_pharmacist FOREIGN KEY (pharmacist_id) REFERENCES pharmacists(id) ON DELETE CASCADE,
    CONSTRAINT fk_assoc_appointments_modality FOREIGN KEY (appointments_modality_id) REFERENCES appointments_modality(id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER set_timestamp BEFORE UPDATE ON pharmacists_appointments_modality FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();