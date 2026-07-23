CREATE TABLE recipient_lists (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    recipient_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX uk_recipient_lists_name
    ON recipient_lists(name);

CREATE TABLE recipients (
    id UUID PRIMARY KEY,
    recipient_list_id UUID NOT NULL,
    email VARCHAR(320) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_recipients_recipient_list
        FOREIGN KEY (recipient_list_id)
        REFERENCES recipient_lists(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX uk_recipients_list_email
    ON recipients(recipient_list_id, email);

CREATE INDEX idx_recipients_recipient_list_id
    ON recipients(recipient_list_id);

CREATE INDEX idx_recipients_status
    ON recipients(status);