CREATE TABLE recipient_import_jobs (
    id UUID PRIMARY KEY,
    recipient_list_id UUID NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    stored_file_path VARCHAR(1000) NOT NULL,
    status VARCHAR(30) NOT NULL,

    total_rows BIGINT NOT NULL DEFAULT 0,
    processed_rows BIGINT NOT NULL DEFAULT 0,
    imported_rows BIGINT NOT NULL DEFAULT 0,
    duplicate_rows BIGINT NOT NULL DEFAULT 0,
    invalid_rows BIGINT NOT NULL DEFAULT 0,

    error_message TEXT,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_import_jobs_recipient_list
        FOREIGN KEY (recipient_list_id)
        REFERENCES recipient_lists(id)
);

CREATE INDEX idx_recipient_import_jobs_recipient_list_id
    ON recipient_import_jobs(recipient_list_id);

CREATE INDEX idx_recipient_import_jobs_status
    ON recipient_import_jobs(status);

CREATE INDEX idx_recipient_import_jobs_created_at
    ON recipient_import_jobs(created_at);
