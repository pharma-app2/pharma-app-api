CREATE TABLE IF NOT EXISTS operators (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS contract_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS plan_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS coverage_scopes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS health_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Nome comercial do plano de saúde (ex: "Amil 450", "Unimed Pleno", "Bradesco Saúde Top Nacional").
    plan_name VARCHAR(100) NOT NULL,

    -- Nome da operadora que oferece o plano (ex: "Amil", "Unimed Rio", "Bradesco Saúde").
    operator_id UUID NOT NULL,

    -- Código de registro do plano na Agência Nacional de Saúde Suplementar (ANS).
    -- É um identificador único e oficial para cada plano no Brasil.
    ans_registration_code VARCHAR(30) NOT NULL,

    -- Tipo de contratação do plano.
    -- Exemplos: 'Individual ou Familiar', 'Coletivo por Adesão', 'Coletivo Empresarial'.
    contract_type_id UUID NOT NULL,

    -- Segmentação assistencial do plano.
    -- Exemplos: 'Ambulatorial', 'Hospitalar sem Obstetrícia', 'Hospitalar com Obstetrícia', 'Odontológico', 'Referência'.
    plan_type_id UUID NOT NULL,

    -- Abrangência geográfica do plano.
    -- Exemplos: 'Nacional', 'Estadual', 'Grupo de Municípios'.
    coverage_scope_id UUID NOT NULL,

    -- Indica se o plano está atualmente ativo e pode ser aceito ou contratado.
    -- Útil para "desativar" planos sem precisar deletá-los (soft delete).
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Data e hora de quando o registro foi criado.
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Data e hora da última atualização do registro.
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Garante que cada plano tenha um código de registro único da ANS.
    CONSTRAINT uk_ans_registration_code UNIQUE (ans_registration_code),

    CONSTRAINT fk_health_plans_operators
        FOREIGN KEY (operator_id) REFERENCES operators(id),

    CONSTRAINT fk_health_plans_contract_types
        FOREIGN KEY (contract_type_id) REFERENCES contract_types(id),

    CONSTRAINT fk_health_plans_plan_types
        FOREIGN KEY (plan_type_id) REFERENCES plan_types(id),

    CONSTRAINT fk_health_plans_coverage_scopes
        FOREIGN KEY (coverage_scope_id) REFERENCES coverage_scopes(id)
);

-- (Opcional) Cria um índice na coluna de nome para acelerar as buscas.
CREATE INDEX IF NOT EXISTS idx_health_plans_plan_name ON health_plans(plan_name);

-- Seeding

INSERT INTO operators (name) VALUES ('Amil'), ('Bradesco Saúde'), ('NotreDame Intermédica'), ('SulAmérica'), ('Unimed Rio') ON CONFLICT (name) DO NOTHING;
INSERT INTO contract_types (name) VALUES ('Individual ou Familiar'), ('Coletivo por Adesão'), ('Coletivo Empresarial') ON CONFLICT (name) DO NOTHING;
INSERT INTO plan_types (name) VALUES ('Ambulatorial'), ('Hospitalar sem Obstetrícia'), ('Hospitalar com Obstetrícia'), ('Odontológico'), ('Referência') ON CONFLICT (name) DO NOTHING;
INSERT INTO coverage_scopes (name) VALUES ('Nacional'), ('Estadual'), ('Grupo de Municípios'), ('Municipal') ON CONFLICT (name) DO NOTHING;

INSERT INTO health_plans (
    plan_name,
    ans_registration_code,
    operator_id,
    contract_type_id,
    plan_type_id,
    coverage_scope_id
) VALUES
-- Plano 1: Amil, empresarial, completo, para o estado de SP (fictício)
(
    'Amil Fácil S75 SP',
    '493829-1',
    (SELECT id FROM operators WHERE name = 'Amil'),
    (SELECT id FROM contract_types WHERE name = 'Coletivo Empresarial'),
    (SELECT id FROM plan_types WHERE name = 'Hospitalar com Obstetrícia'),
    (SELECT id FROM coverage_scopes WHERE name = 'Estadual')
),

-- Plano 2: Bradesco, empresarial, plano de referência nacional
(
    'Bradesco Saúde Top Nacional Plus',
    '412345-6',
    (SELECT id FROM operators WHERE name = 'Bradesco Saúde'),
    (SELECT id FROM contract_types WHERE name = 'Coletivo Empresarial'),
    (SELECT id FROM plan_types WHERE name = 'Referência'),
    (SELECT id FROM coverage_scopes WHERE name = 'Nacional')
),

-- Plano 3: SulAmérica, por adesão, nacional, mas sem parto
(
    'SulAmérica Exato',
    '487654-3',
    (SELECT id FROM operators WHERE name = 'SulAmérica'),
    (SELECT id FROM contract_types WHERE name = 'Coletivo por Adesão'),
    (SELECT id FROM plan_types WHERE name = 'Hospitalar sem Obstetrícia'),
    (SELECT id FROM coverage_scopes WHERE name = 'Nacional')
),

-- Plano 4: Unimed Rio, individual, apenas ambulatorial e para o município do Rio
(
    'Unimed Rio Personal 2',
    '478912-2',
    (SELECT id FROM operators WHERE name = 'Unimed Rio'),
    (SELECT id FROM contract_types WHERE name = 'Individual ou Familiar'),
    (SELECT id FROM plan_types WHERE name = 'Ambulatorial'),
    (SELECT id FROM coverage_scopes WHERE name = 'Municipal')
),

-- Plano 5: NotreDame, empresarial, para um grupo de municípios
(
    'NotreDame Smart 200',
    '465432-1',
    (SELECT id FROM operators WHERE name = 'NotreDame Intermédica'),
    (SELECT id FROM contract_types WHERE name = 'Coletivo Empresarial'),
    (SELECT id FROM plan_types WHERE name = 'Referência'),
    (SELECT id FROM coverage_scopes WHERE name = 'Grupo de Municípios')
),

-- Plano 6: Amil, individual, apenas odontológico e de abrangência nacional
(
    'Amil Dental 205',
    '454321-0',
    (SELECT id FROM operators WHERE name = 'Amil'),
    (SELECT id FROM contract_types WHERE name = 'Individual ou Familiar'),
    (SELECT id FROM plan_types WHERE name = 'Odontológico'),
    (SELECT id FROM coverage_scopes WHERE name = 'Nacional')
) ON CONFLICT (ans_registration_code) DO NOTHING;