ALTER TABLE patients ADD ibge_api_city_id INTEGER NOT NULL DEFAULT 0;
ALTER TABLE pharmacists ADD accepts_remote BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS pharmacist_locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pharmacist_id UUID NOT NULL,
    ibge_api_city_id UUID NOT NULL,
    address TEXT NOT NULL,
    phone1 VARCHAR(15),
    phone2 VARCHAR(15),
    phone3 VARCHAR(15),

    CONSTRAINT fk_locations_pharmacists FOREIGN KEY (pharmacist_id) REFERENCES pharmacists(id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER set_timestamp BEFORE UPDATE ON pharmacist_locations FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();

CREATE INDEX IF NOT EXISTS idx_locations_pharmacist_id ON pharmacist_locations(pharmacist_id);
