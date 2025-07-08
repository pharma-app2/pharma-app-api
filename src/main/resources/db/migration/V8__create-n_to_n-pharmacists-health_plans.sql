-- Passo 3: Criar a tabela intermediária 'health_plans_pharmacists'
CREATE TABLE IF NOT EXISTS health_plans_pharmacists (
    -- Chave estrangeira para a tabela 'pharmacists'
    pharmacist_id UUID NOT NULL,

    -- Chave estrangeira para a tabela 'health_plans'
    health_plan_id UUID NOT NULL,

    -- Colunas de auditoria para a associação
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Chave primária composta: garante que um farmacêutico não pode ser associado
    -- ao mesmo plano de saúde mais de uma vez.
    PRIMARY KEY (pharmacist_id, health_plan_id),

    -- Constraints de chave estrangeira
    CONSTRAINT fk_assoc_pharmacists
        FOREIGN KEY (pharmacist_id) REFERENCES pharmacists(id) ON DELETE CASCADE,

    CONSTRAINT fk_assoc_health_plans
        FOREIGN KEY (health_plan_id) REFERENCES health_plans(id) ON DELETE CASCADE
);

-- Trigger para atualizar 'updated_at' na tabela de associação
CREATE OR REPLACE TRIGGER set_timestamp BEFORE UPDATE ON health_plans_pharmacists FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
