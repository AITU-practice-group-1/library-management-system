CREATE TABLE IF NOT EXISTS email_verification_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    user_id UUID UNIQUE,
    expiry_date TIMESTAMP,

    CONSTRAINT fk_token_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);