ALTER TABLE appointments
DROP CONSTRAINT IF EXISTS uk_appointments_pharmacists;

ALTER TABLE appointments
DROP CONSTRAINT IF EXISTS uk_availabilities_pharmacist_id;

ALTER TABLE appointments
DROP COLUMN IF EXISTS pharmacist_id;