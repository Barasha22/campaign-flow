CREATE TABLE message_templates (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    subject VARCHAR(250),
    body TEXT NOT NULL,
    channel VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_message_templates_channel
    ON message_templates(channel);

CREATE UNIQUE INDEX uk_message_templates_name
    ON message_templates(name);