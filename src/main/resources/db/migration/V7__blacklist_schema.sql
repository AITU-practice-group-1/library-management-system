CREATE TABLE IF NOT EXISTS blacklist (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    reason TEXT NOT NULL,
    days_overdue INTEGER,
    added_at TIMESTAMP NOT NULL,
    resolved BOOLEAN DEFAULT FALSE
);

