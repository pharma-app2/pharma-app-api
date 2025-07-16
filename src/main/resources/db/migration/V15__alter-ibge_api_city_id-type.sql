-- Passo 1: Remove a coluna antiga
ALTER TABLE pharmacist_locations DROP COLUMN ibge_api_city_id;

-- Passo 2: Adiciona a nova coluna com o tipo correto
ALTER TABLE pharmacist_locations ADD COLUMN ibge_api_city_id INTEGER NOT NULL;