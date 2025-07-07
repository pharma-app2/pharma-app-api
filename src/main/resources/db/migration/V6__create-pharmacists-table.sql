CREATE TABLE IF NOT EXISTS pharmacists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    crf VARCHAR(20) NOT NULL UNIQUE,
    user_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_pharmacists_users
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT uk_pharmacists_crf
        UNIQUE (crf)
);