-- Flyway runs versioned migrations before Hibernate can create the original
-- employee tables. Bootstrap the two legacy tables on a brand-new database so
-- V1 and V2 can safely evolve them. Existing installations are left unchanged.
CREATE TABLE IF NOT EXISTS employee_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    keycloak_id VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(255) UNIQUE,
    hire_date DATE,
    position VARCHAR(255),
    role_id BIGINT,
    department_id BIGINT
);
