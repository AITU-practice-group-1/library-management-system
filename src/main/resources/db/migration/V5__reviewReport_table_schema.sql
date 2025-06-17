CREATE TABLE review_report (
    id UUID PRIMARY KEY,
    feedback_id UUID NOT NULL,
    user_id UUID NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_report_on_feedback
        FOREIGN KEY (feedback_id)
            REFERENCES feedback(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_report_by_use
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);