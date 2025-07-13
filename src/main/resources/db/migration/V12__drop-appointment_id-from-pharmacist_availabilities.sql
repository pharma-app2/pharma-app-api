-- Passo 1: Remover a constraint de chave estrangeira que aponta para a tabela 'appointments'.
ALTER TABLE pharmacist_availabilities
DROP CONSTRAINT IF EXISTS fk_availabilities_appointments;

-- Passo 2: Remover a constraint de unicidade da coluna.
ALTER TABLE pharmacist_availabilities
DROP CONSTRAINT IF EXISTS uk_availabilities_appointment_id;

-- Passo 3: Finalmente, remover a coluna em si.
ALTER TABLE pharmacist_availabilities
DROP COLUMN IF EXISTS appointment_id;