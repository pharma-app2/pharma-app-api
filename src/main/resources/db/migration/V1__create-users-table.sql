  CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,

    CONSTRAINT uk_user_email UNIQUE (email)
);