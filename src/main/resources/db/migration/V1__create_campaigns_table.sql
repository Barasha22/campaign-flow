CREATE TABLE campaigns (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(30) NOT NULL,
    scheduled_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_campaigns_status
    ON campaigns(status);

CREATE INDEX idx_campaigns_scheduled_at
    ON campaigns(scheduled_at);