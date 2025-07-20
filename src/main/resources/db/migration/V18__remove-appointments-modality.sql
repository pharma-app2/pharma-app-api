-- Passo 1: Remover a constraint de chave estrangeira na tabela 'appointments'
-- Esta é a constraint que depende da tabela 'appointments_modality'.
ALTER TABLE appointments
DROP CONSTRAINT IF EXISTS fk_appointments_modality;

-- Passo 2: Apagar a coluna de chave estrangeira da tabela 'appointments'
ALTER TABLE appointments
DROP COLUMN IF EXISTS appointments_modality_id;

-- Passo 3: Apagar a tabela de junção entre farmacêuticos e modalidades.
-- Como ela depende tanto de 'pharmacists' quanto de 'appointments_modality',
-- ela deve ser removida antes de apagar 'appointments_modality'.
DROP TABLE IF EXISTS pharmacists_appointments_modality;

-- Passo 4: Finalmente, apagar a tabela principal de modalidades.
DROP TABLE IF EXISTS appointments_modality;

ALTER TABLE appointments
ADD COLUMN is_remote BOOLEAN NOT NULL DEFAULT false;