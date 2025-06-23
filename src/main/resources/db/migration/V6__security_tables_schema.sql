CREATE TABLE two_factor_auth (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    secret VARCHAR(100) NOT NULL,
    is_enabled BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE password_reset_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    user_id UUID NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
