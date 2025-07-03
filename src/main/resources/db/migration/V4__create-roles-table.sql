ALTER TABLE users ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE patients ALTER COLUMN id SET DEFAULT gen_random_uuid();

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(15) NOT NULL,

    CONSTRAINT uk_role_name UNIQUE (name)
);

INSERT INTO roles (name) VALUES ('ROLE_PATIENT');
INSERT INTO roles (name) VALUES ('ROLE_PHARMACIST');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

ALTER TABLE users ADD COLUMN role_id UUID;

UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_PATIENT') WHERE role = 'patient';
UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_PHARMACIST') WHERE role = 'pharmacist';
UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN') WHERE role = 'admin';

ALTER TABLE users ALTER COLUMN role_id SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT fk_user_role
    FOREIGN KEY (role_id)
    REFERENCES roles(id);

ALTER TABLE users DROP COLUMN role;