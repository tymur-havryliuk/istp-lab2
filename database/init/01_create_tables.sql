DROP TABLE IF EXISTS repair_requests CASCADE;
DROP TABLE IF EXISTS users CASCADE;

DROP TYPE IF EXISTS request_priority CASCADE;
DROP TYPE IF EXISTS request_status CASCADE;
DROP TYPE IF EXISTS equipment_type CASCADE;
DROP TYPE IF EXISTS user_role CASCADE;

CREATE TYPE user_role AS ENUM ('USER', 'TECHNICIAN');

CREATE TYPE equipment_type AS ENUM (
    'PRINTER',
    'LAPTOP',
    'COMPUTER',
    'PROJECTOR',
    'ROUTER',
    'MONITOR',
    'OTHER'
);

CREATE TYPE request_status AS ENUM (
    'CREATED',
    'IN_PROGRESS',
    'WAITING_FOR_USER',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE request_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH');

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE repair_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    technician_id BIGINT REFERENCES users(id),
    equipment_type equipment_type NOT NULL,
    equipment_name VARCHAR(150),
    title VARCHAR(200) NOT NULL,
    problem_description TEXT NOT NULL,
    user_comment TEXT,
    technician_comment TEXT,
    repair_result TEXT,
    status request_status NOT NULL DEFAULT 'CREATED',
    priority request_priority NOT NULL DEFAULT 'MEDIUM',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    closed_at TIMESTAMP,
    CONSTRAINT chk_repair_requests_other_equipment_name
        CHECK (
            equipment_type <> 'OTHER'
            OR NULLIF(BTRIM(equipment_name), '') IS NOT NULL
        ),
    CONSTRAINT chk_repair_requests_completed_requires_result
        CHECK (
            status <> 'COMPLETED'
            OR NULLIF(BTRIM(repair_result), '') IS NOT NULL
        ),
    CONSTRAINT chk_repair_requests_cancelled_requires_cancelled_at
        CHECK (status <> 'CANCELLED' OR cancelled_at IS NOT NULL),
    CONSTRAINT chk_repair_requests_completed_requires_closed_at
        CHECK (status <> 'COMPLETED' OR closed_at IS NOT NULL)
);

CREATE INDEX idx_repair_requests_user_id
    ON repair_requests (user_id);

CREATE INDEX idx_repair_requests_technician_id
    ON repair_requests (technician_id);

CREATE INDEX idx_repair_requests_status
    ON repair_requests (status);

CREATE INDEX idx_repair_requests_created_at
    ON repair_requests (created_at);
