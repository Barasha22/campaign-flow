ALTER TABLE campaigns
ADD COLUMN template_id UUID;

ALTER TABLE campaigns
ADD CONSTRAINT fk_campaigns_template
FOREIGN KEY (template_id)
REFERENCES message_templates(id);

CREATE INDEX idx_campaigns_template_id
ON campaigns(template_id);