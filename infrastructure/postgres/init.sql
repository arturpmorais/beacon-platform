-- schemas are isolated by service on a single database
-- each service can only read/write its own schema

CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS audit;

-- user service

CREATE TABLE IF NOT EXISTS users.users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id VARCHAR(255) NOT NULL UNIQUE,  -- id from upstream system
    email       VARCHAR(255),
    phone       VARCHAR(50),
    push_token  VARCHAR(512),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS users.notification_preferences (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users.users(id) ON DELETE CASCADE,
    channel         VARCHAR(20) NOT NULL,   -- EMAIL | SMS | PUSH
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    quiet_start     TIME,
    quiet_end       TIME, 
    UNIQUE (user_id, channel)
);

-- audit service

CREATE TABLE IF NOT EXISTS audit.notification_logs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notification_id     UUID NOT NULL,
    user_id             VARCHAR(255) NOT NULL,
    channel             VARCHAR(20) NOT NULL,
    status              VARCHAR(20) NOT NULL,
    error_message       TEXT,
    provider_response   TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_notification_id ON audit.notification_logs(notification_id);
CREATE INDEX IF NOT EXISTS idx_audit_user_id         ON audit.notification_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_created_at      ON audit.notification_logs(created_at DESC);

-- seed: user for testing

INSERT INTO users.users (external_id, email, phone, push_token)
VALUES ('user-001', 'user@test.com', '+5511999999999', 'mock-push-token-001')
ON CONFLICT (external_id) DO NOTHING;

INSERT INTO users.notification_preferences (user_id, channel, enabled)
SELECT id, 'EMAIL', true FROM users.users WHERE external_id = 'user-001'
ON CONFLICT (user_id, channel) DO NOTHING;

INSERT INTO users.notification_preferences (user_id, channel, enabled)
SELECT id, 'SMS', true FROM users.users WHERE external_id = 'user-001'
ON CONFLICT (user_id, channel) DO NOTHING;

INSERT INTO users.notification_preferences (user_id, channel, enabled)
SELECT id, 'PUSH', true FROM users.users WHERE external_id = 'user-001'
ON CONFLICT (user_id, channel) DO NOTHING;