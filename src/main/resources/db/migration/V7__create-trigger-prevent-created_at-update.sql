-- Adiciona as colunas à tabela 'users'
ALTER TABLE users ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE patients ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE roles ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- Esta função será chamada antes de cada UPDATE na tabela
CREATE OR REPLACE FUNCTION prevent_created_at_update()
RETURNS TRIGGER AS $$
BEGIN
    -- 'OLD' representa a linha como ela estava ANTES do update.
    -- 'NEW' representa a linha como ela ficará DEPOIS do update.
    -- Usamos 'IS DISTINCT FROM' para tratar corretamente valores NULL.
    IF NEW.created_at IS DISTINCT FROM OLD.created_at THEN
        -- Se o valor de 'created_at' estiver sendo alterado, lançamos um erro.
        RAISE EXCEPTION 'Column "created_at" is not updatable';
    END IF;

    -- Se a coluna 'created_at' não foi alterada, a operação é permitida.
    -- Retornamos 'NEW' para que o update das outras colunas possa continuar.
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_prevent_created_at_update BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER trg_roles_prevent_created_at_update BEFORE UPDATE ON roles FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER trg_patients_prevent_created_at_update BEFORE UPDATE ON patients FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER trg_pharmacists_prevent_created_at_update BEFORE UPDATE ON pharmacists FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER trg_health_plans_prevent_created_at_update BEFORE UPDATE ON health_plans FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER trg_operators_prevent_created_at_update BEFORE UPDATE ON operators FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER trg_contract_types_prevent_created_at_update BEFORE UPDATE ON contract_types FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER trg_plan_types_prevent_created_at_update BEFORE UPDATE ON plan_types FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER trg_coverage_scopes_prevent_created_at_update BEFORE UPDATE ON coverage_scopes FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  -- Define o campo 'updated_at' da nova versão da linha ('NEW')
  -- para o tempo atual antes de o update ser salvo.
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para a tabela 'users'
CREATE TRIGGER set_timestamp_users BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER set_timestamp_patients BEFORE UPDATE ON patients FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER set_timestamp_roles BEFORE UPDATE ON roles FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER set_timestamp_pharmacists BEFORE UPDATE ON pharmacists FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER set_timestamp_health_plans BEFORE UPDATE ON health_plans FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER set_timestamp_operators BEFORE UPDATE ON operators FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER set_timestamp_contract_types BEFORE UPDATE ON contract_types FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER set_timestamp_plan_types BEFORE UPDATE ON plan_types FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER set_timestamp_coverage_scopes BEFORE UPDATE ON coverage_scopes FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();