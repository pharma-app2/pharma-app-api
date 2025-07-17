ALTER TABLE pharmacist_locations
    ADD COLUMN ibge_api_city VARCHAR(100),
    ADD COLUMN ibge_api_state VARCHAR(100),
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();


SELECT id, pharmacist_id, ibge_api_city, ibge_api_state
FROM pharmacist_locations
WHERE ibge_api_city IS NULL OR ibge_api_state IS NULL;

UPDATE pharmacist_locations
SET
    ibge_api_city = 'Cidade não informada',
    ibge_api_state = 'XX'
WHERE
    ibge_api_city IS NULL OR ibge_api_state IS NULL;

-- Se você já tem dados na tabela, precisará preencher os valores antes de adicionar a restrição NOT NULL.
-- Exemplo: UPDATE pharmacist_locations SET ibge_api_city = 'Nome da Cidade', ibge_api_state = 'Nome do Estado' WHERE ...;

-- Depois de popular os dados, adicione a restrição NOT NULL
ALTER TABLE pharmacist_locations
    ALTER COLUMN ibge_api_city SET NOT NULL,
    ALTER COLUMN ibge_api_state SET NOT NULL;